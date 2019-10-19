package com.chenweikeng.fabric_python;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.ChunkCache;
import org.lwjgl.glfw.GLFW;
import py4j.GatewayServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class PythonProxy implements ModInitializer {
	public String curAuthToken;
	private static PythonProxy pythonProxy;
	public static long lastHotKeyShowAuthToken = 0L;

	private static final String KEYBIND_CATEGORY = "key.fabric_python.category";
	private static final Identifier HOTKEY_SHOW_AUTH_TOKEN = new Identifier("fabric_python", "show_auth_token");
	private static FabricKeyBinding showAuthToken;

	public static PythonProxy getInstance(){
		return pythonProxy;
	}

	public void sendMessageToPlayer(String str) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.player.sendMessage(new LiteralText(str));
	}

	@SuppressWarnings("unused")
	public void movePlayer(int delta_x, int delta_y, int delta_z){
		MinecraftClient client = MinecraftClient.getInstance();
		client.player.move(MovementType.PLAYER, new Vec3d(delta_x, delta_y, delta_z));
	}

	@SuppressWarnings("unused")
	public String movePlayerAI(int dest_x, int dest_y, int dest_z) throws InterruptedException {
		MinecraftClient client = MinecraftClient.getInstance();

		PathNodeMaker nodeMaker = new LandPathNodeMaker();
		nodeMaker.setCanEnterOpenDoors(true);
		PathNodeNavigator pathNodeNavigator = new PathNodeNavigator(nodeMaker, MathHelper.floor(100 * 16.0D)); // as in the case of a ghast

		FakePlayerMob fakePlayerMob = new FakePlayerMob(FakePlayerMob.FAKE_PLAYER_MOB_ENTITY_TYPE, client.player.world);
		fakePlayerMob.copyPlayer(client.player);

		String result = "";

		int stepCountMax = 20;
		int stepCount = 0;
		while(stepCount++ < stepCountMax && client.player.getPos().distanceTo(new Vec3d(dest_x, dest_y, dest_z)) > 1.00){
			int rangeOfViewableWorld = 100 + 16;
			BlockPos playerBlockPos = client.player.getBlockPos();
			fakePlayerMob.copyPlayer(client.player);
			ViewableWorld viewableWorld = new ChunkCache(client.player.world, playerBlockPos.add(-rangeOfViewableWorld, -rangeOfViewableWorld, -rangeOfViewableWorld), playerBlockPos.add(rangeOfViewableWorld, rangeOfViewableWorld, rangeOfViewableWorld));
			Path path = pathNodeNavigator.pathfind(viewableWorld, fakePlayerMob, ImmutableSet.of(new BlockPos(dest_x, dest_y, dest_z)), 100, 1);

			if(path == null) {
				break;
			}

			Vec3d dest = client.player.getPos();
			while(path.getCurrentNodeIndex() < path.getLength()) {
				Vec3d next = path.getCurrentPosition();
				if(next.distanceTo(client.player.getPos()) < 10){
					dest = next;
					path.setCurrentNodeIndex(path.getCurrentNodeIndex() + 1);
				}else{
					Vec3d delta = dest.subtract(client.player.getPos());
					result = String.format("%snext step: %f %f %f\n", result, delta.x, delta.y, delta.z);

					client.player.setJumping(true);
					client.player.setYaw(changeAngle(client.player.yaw, (float)(MathHelper.atan2(delta.z, delta.x) * 57.2957763671875D) - 90.0F, 90.0F));
					client.player.move(MovementType.PLAYER, delta);
					client.player.setJumping(false);

					TimeUnit.MILLISECONDS.sleep(100);

					if(dest.distanceTo(client.player.getPos()) > 4){
						break;
					}
				}
			}

			if(dest.distanceTo(client.player.getPos()) > 1){
				Vec3d delta = dest.subtract(client.player.getPos());
				result = String.format("%sfinal step: %f %f %f\n", result, delta.x, delta.y, delta.z);

				client.player.setJumping(true);
				client.player.setYaw(changeAngle(client.player.yaw, (float)(MathHelper.atan2(delta.z, delta.x) * 57.2957763671875D) - 90.0F, 90.0F));
				client.player.move(MovementType.PLAYER, delta);
				client.player.setJumping(false);
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public Integer[] getPlayerLocation(){
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		return new Integer[] {(int)player.x, (int)player.y, (int)player.z};
	}

	@SuppressWarnings("unused")
	public void sendChatMessage(String message) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		player.sendChatMessage(message);
	}

	@SuppressWarnings("unused")
	public void changePlayerPosition(float yaw_360, float pitch_360){
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		player.yaw = MathHelper.clamp(yaw_360, -180.0F, 180.0F);
		player.pitch = MathHelper.clamp(pitch_360, -90.0F, 90.0F);
	}

	@SuppressWarnings("unused")
	public void attackBlock(int x, int y, int z) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;

		Vec3d player_pos = player.getPos();
		Vec3d target_pos = new Vec3d(x, y, z);
		Vec3d delta = player_pos.subtract(target_pos);
		client.interactionManager.attackBlock(new BlockPos(x, y, z), Direction.getFacing(delta.x, delta.y, delta.z));
	}

	@SuppressWarnings("unused")
	public String switchItem(String item_name) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;

		if(player.getMainHandStack() != null && player.getMainHandStack().getItem() == Registry.ITEM.get(new Identifier(item_name)) && player.getMainHandStack().getCount() >= 1) {
			return "Success";
		}

		/* find the itemstack among the item boxes */
		List<Slot> slotList = player.playerContainer.slotList;

		/* skip slot 0: crafting result */
		/* skip slot 1,2,3,4: the 2x2 crafting tables */
		/* skip slot 5,6,7,8: armors */

		/* available slots:
		 * first column:  9, 10, 11,    12, 13, 14,    15, 16, 17
		 * second column: 18, 19, 20,    21, 22, 23,    24, 25, 26
		 * third column:  27, 28, 29,    30, 31, 32,    33, 34, 35
		 * 1-9: 36, 37, 38    39, 40, 41    42, 43, 44
		 */
		for(int i = 9; i <= 44; i++) {
			Slot cur_slot = player.playerContainer.getSlot(i);
			if(cur_slot.hasStack() && cur_slot.getStack().getItem() == Registry.ITEM.get(new Identifier(item_name)) && cur_slot.getStack().getCount() >= 1){
				if(i != 36){
					client.interactionManager.method_2906(player.playerContainer.syncId, i, 0, SlotActionType.SWAP, player);
				}
				player.inventory.selectedSlot = 0;
				return "Success";
			}
		}

		return "Failure";
	}

	@SuppressWarnings("unused")
	public String useBlock(int x, int y, int z) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;

		Vec3d player_pos = player.getPos();
		Vec3d target_pos = new Vec3d(x, y, z);
		Vec3d delta = player_pos.subtract(target_pos);

		BlockHitResult hitResult = new BlockHitResult(target_pos, Direction.getFacing(delta.x, delta.y, delta.z), new BlockPos(target_pos), false);

		Hand hand_1 = Hand.MAIN_HAND;
		ItemStack itemStack_1 = player.getStackInHand(hand_1);

		int int_1 = itemStack_1.getCount();
		ActionResult actionResult_1 = client.interactionManager.interactBlock(player, player.clientWorld, hand_1, hitResult);
		if (actionResult_1 == ActionResult.SUCCESS) {
			player.swingHand(hand_1);
			if (!itemStack_1.isEmpty() && (itemStack_1.getCount() != int_1 || client.interactionManager.hasCreativeInventory())) {
				client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand_1);
			}

			return "Success";
		}

		if (actionResult_1 == ActionResult.FAIL) {
			return "Failure";
		}

		return "Unknown";
	}

	@SuppressWarnings("unused")
	public List<Integer[]> blockSearch(int start_x, int start_y, int start_z, int end_x, int end_y, int end_z, String typename) throws NoSuchFieldException, IllegalAccessException {
		Block target_block = (Block) Registry.BLOCK.get(new Identifier(typename));
		if(target_block == null){
			return new ArrayList<Integer[]>();
		}

		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		List<Integer[]> list = new ArrayList<Integer[]>();

		if(end_x < start_x) end_x = start_x;
		if(end_y < start_y) end_y = start_y;
		if(end_z < start_z) end_z = start_z;

		for(int i = start_x; i <= end_x; i++){
			for(int j = start_y; j <= end_y; j++){
				for(int k = start_z; k <= end_z; k++){
					BlockState bst = player.world.getBlockState(new BlockPos(i, j, k));
					if(bst.getBlock() == target_block){
						list.add(new Integer[] {i, j, k});
					}
				}
			}
		}

		return list;
	}

	@Override
	public void onInitialize() {
		pythonProxy = this;

		curAuthToken = AuthToken.randomAuthToken();
		curAuthToken = "insecure";
		GatewayServer server = new GatewayServer.GatewayServerBuilder().entryPoint(
				new PythonProxy()).authToken(curAuthToken).build();
		server.start();

		FakePlayerMob.registerFakePlayerMob();

		KeyBindingRegistryImpl.INSTANCE.addCategory(KEYBIND_CATEGORY);
		KeyBindingRegistryImpl.INSTANCE.register(showAuthToken = FabricKeyBinding.Builder.create(HOTKEY_SHOW_AUTH_TOKEN, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, KEYBIND_CATEGORY).build());

		ClientTickCallback.EVENT.register(e ->
		{
			PythonProxy pp = PythonProxy.getInstance();
			long curUnixTime = System.currentTimeMillis() / 1000L;
			if(showAuthToken.isPressed() && ((curUnixTime - pp.lastHotKeyShowAuthToken)> 3)) {
				sendMessageToPlayer(String.format("[Fabric-Python] Authentication token is %s", pp.curAuthToken));
				pp.lastHotKeyShowAuthToken = curUnixTime;
			}
		});
	}

	float changeAngle(float float_1, float float_2, float float_3) {
		float float_4 = MathHelper.wrapDegrees(float_2 - float_1);
		if (float_4 > float_3) {
			float_4 = float_3;
		}

		if (float_4 < -float_3) {
			float_4 = -float_3;
		}

		float float_5 = float_1 + float_4;
		if (float_5 < 0.0F) {
			float_5 += 360.0F;
		} else if (float_5 > 360.0F) {
			float_5 -= 360.0F;
		}

		return float_5;
	}
}

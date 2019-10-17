package com.chenweikeng.fabric_python;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.ChunkCache;
import org.lwjgl.glfw.GLFW;
import py4j.GatewayServer;

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
		while(stepCount++ < stepCountMax && (client.player.getPos().distanceTo(new Vec3d(dest_x, dest_y, dest_z)) > 8.00 || Math.abs(client.player.getPos().y - dest_y) >= 1)){
			int rangeOfViewableWorld = 100 + 16;
			BlockPos playerBlockPos = client.player.getBlockPos();
			fakePlayerMob.copyPlayer(client.player);
			ViewableWorld viewableWorld = new ChunkCache(client.player.world, playerBlockPos.add(-rangeOfViewableWorld, -rangeOfViewableWorld, -rangeOfViewableWorld), playerBlockPos.add(rangeOfViewableWorld, rangeOfViewableWorld, rangeOfViewableWorld));
			Path path = pathNodeNavigator.pathfind(viewableWorld, fakePlayerMob, ImmutableSet.of(new BlockPos(dest_x, dest_y, dest_z)), 100, 1);

			if(path == null) {
				break;
			}

			Vec3d delta;
			Vec3d old_cur = client.player.getPos();
			int flag = 0;
			while(path.getCurrentNodeIndex() < path.getLength() && flag != 2) {
				Vec3d cur = client.player.getPos();

				Vec3d next = path.getCurrentPosition();
				path.setCurrentNodeIndex(path.getCurrentNodeIndex() + 1);

				while(next.distanceTo(cur) > 1){
					cur = client.player.getPos();
					if(flag == 1 && old_cur.distanceTo(cur) < 1){
						flag = 2;
						break;
					}
					old_cur = cur;
					flag = 1;

					delta = next.subtract(cur);
					if(delta.lengthSquared() > 80){
						double suppress = Math.sqrt(delta.lengthSquared() / 80);
						delta = new Vec3d(delta.x / suppress, delta.y / suppress, delta.z / suppress);
					}

					result = String.format("%snext step: %f %f %f\n", result, delta.x, delta.y, delta.z);
					Vec3d cur_dest = cur.add(delta);
					client.player.move(MovementType.PLAYER, delta);
					TimeUnit.MILLISECONDS.sleep(100);

					cur = client.player.getPos();
				}
			}
		}
		return result;
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
}

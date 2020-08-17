package org.fabric_python.mod.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.AirBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;

@Mixin(WorldRenderer.class)
public class CrossRenderMixin {
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V",
            ordinal = 0))
    public void renderSymbols(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        assert player != null;

        if(!player.isOnGround()) return;

        boolean isXrayOn = PythonProxy.globalMap != null && PythonProxy.globalMap.getOrDefault("xray", "0").equals("1");
        if(!isXrayOn) return;

        World world = player.world;

        BlockPos playerPos = player.getBlockPos();
        Camera gameCamera = client.gameRenderer.getCamera();

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(3.0F);
        GL11.glBegin(GL11.GL_LINES);

        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        int playerZ = playerPos.getZ();

        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                boolean flagSafe = false;
                boolean flagUnsafe = false;
                for (int k = -2; k <= 1; k++) {
                    BlockPos blockPos = new BlockPos(playerX + i, playerY + k, playerZ + j);
                    BlockPos blockPos_up = new BlockPos(playerX + i, playerY + k + 1, playerZ + j);
                    BlockPos blockPos_up2 = new BlockPos(playerX + i, playerY + k + 2, playerZ + j);
                    BlockPos blockPos_down = new BlockPos(playerX + i, playerY + k - 1, playerZ + j);

                    int color = 0x000000;
                    if (world.getBlockState(blockPos_up).getBlock().getClass().getSimpleName().equals(AirBlock.class.getSimpleName())
                            && world.getBlockState(blockPos_up2).getBlock().getClass().getSimpleName().equals(AirBlock.class.getSimpleName())
                            && !world.getBlockState(blockPos).getBlock().getClass().getSimpleName().equals(AirBlock.class.getSimpleName())) {
                        color = 0x0CA734;
                        flagSafe = true;
                    } else {
                        if ( !flagSafe && !flagUnsafe && k <= 0 && world.getBlockState(blockPos).getBlock().getClass().getSimpleName().equals(AirBlock.class.getSimpleName())
                                && world.getBlockState(blockPos_down).getBlock().getClass().getSimpleName().equals(AirBlock.class.getSimpleName())) {
                            color = 0xFF0000;
                            flagUnsafe = true;
                        }
                    }

                    if (color == 0x000000) {
                        continue;
                    }

                    double d0 = gameCamera.getPos().x;
                    double d1 = gameCamera.getPos().y - .005D;
                    VoxelShape upperOutlineShape = world.getBlockState(blockPos).getOutlineShape(world, blockPos, ShapeContext.of(player));
                    if (!upperOutlineShape.isEmpty())
                        d1 -= upperOutlineShape.getMax(Direction.Axis.Y);
                    double d2 = gameCamera.getPos().z;

                    int red = (color >> 16) & 255;
                    int green = (color >> 8) & 255;
                    int blue = color & 255;
                    int x = blockPos.getX();
                    int y = blockPos.getY();
                    int z = blockPos.getZ();
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1f);
                    GL11.glVertex3d(x + .01 - d0, y - d1, z + .01 - d2);
                    GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z - .01 + 1 - d2);
                    GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z + .01 - d2);
                    GL11.glVertex3d(x + .01 - d0, y - d1, z - .01 + 1 - d2);
                }
            }
        }

        GL11.glEnd();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}

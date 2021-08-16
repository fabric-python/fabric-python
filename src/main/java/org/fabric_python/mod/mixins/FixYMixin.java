package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class FixYMixin {
    @Inject(at = @At("HEAD"), method = "tick ()V")
    private void tick(CallbackInfo info) {
        if (PythonProxy.globalMap == null) {
            return;
        }

        double fixY = Double.parseDouble(PythonProxy.globalMap.getOrDefault("fixy", "0.0"));
        if (fixY == 0.0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        if(player.isOnGround()){
            PythonProxy.globalMap.put("levitation_fixy", "0");
        }else {
            PythonProxy.globalMap.put("levitation_fixy", "1");
            player.move(MovementType.SELF, new Vec3d(0, 1.05 * (fixY - player.getPos().getY()), 0));
        }
    }
}

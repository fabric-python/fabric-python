package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Mixin(ClientPlayerEntity.class)
public class ChatMessageMixin {
    @Inject(at = @At("HEAD"), method = "sendChatMessage (Ljava/lang/String;)V", cancellable = true)
    protected void chatListener(String msg, CallbackInfo info) {
        if (PythonProxy.globalMap != null) {
            if (msg.startsWith("/xray")) {
                int currentXray = Integer.parseInt(PythonProxy.globalMap.getOrDefault("xray", "0"));
                PythonProxy.globalMap.put("xray", String.valueOf(1 - currentXray));

                MinecraftClient.getInstance().worldRenderer.reload();

                info.cancel();
                return;
            }

            if (msg.startsWith("/nightvision")) {
                int currentNV = Integer.parseInt(PythonProxy.globalMap.getOrDefault("night_vision", "0"));
                PythonProxy.globalMap.put("night_vision", String.valueOf(1 - currentNV));

                MinecraftClient.getInstance().worldRenderer.reload();

                info.cancel();
                return;
            }

            if (msg.startsWith("/safefall")) {
                int currentSafeFall = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safefall", "0"));
                PythonProxy.globalMap.put("safefall", String.valueOf(1 - currentSafeFall));

                info.cancel();
                return;
            }

            if (msg.startsWith("/study ")) {
                boolean currentStudy = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("study_locked", "False"));
                long currentStudyUntil = Long.parseLong(PythonProxy.globalMap.getOrDefault("study_locked_until", "0"));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player == null) {
                    info.cancel();
                    return;
                }

                if (currentStudy && currentStudyUntil >= Instant.now().getEpochSecond()) {
                    player.sendMessage(Text.method_30163("You are still in the study mode."), false);
                    info.cancel();
                    return;
                }

                String minutesMsg = msg.replace("/study ", "");
                int minute = 0;
                try {
                    minute = Integer.parseInt(minutesMsg);
                } catch (NumberFormatException ignored) {

                }

                if (minute <= 0) {
                    player.sendMessage(Text.method_30163("Minutes are not a positive number."), false);
                    info.cancel();
                    return;
                }

                PythonProxy.globalMap.put("study_locked_until", String.valueOf(Instant.now().getEpochSecond() + minute * 60));
                PythonProxy.globalMap.put("study_locked", "True");

                player.sendMessage(Text.method_30163("Study mode is on."), false);
                info.cancel();
            }
        }
    }
}

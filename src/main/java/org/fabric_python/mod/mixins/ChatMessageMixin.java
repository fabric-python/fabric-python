package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.Text;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(ClientPlayerEntity.class)
public class ChatMessageMixin {
    @Inject(at = @At("HEAD"), method = "sendChatMessage (Ljava/lang/String;)V", cancellable = true)
    protected void chatListener(String msg, CallbackInfo info) {
        if (PythonProxy.globalMap != null) {
            if (msg.startsWith("/xray")) {
                int currentXray = Integer.parseInt(PythonProxy.globalMap.getOrDefault("xray", "0"));
                PythonProxy.globalMap.put("xray", String.valueOf(1 - currentXray));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentXray == 0) {
                        player.sendMessage(Text.method_30163("Xray is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Xray is off."), false);
                    }
                }

                MinecraftClient.getInstance().worldRenderer.reload();

                info.cancel();
                return;
            }

            if (msg.startsWith("/nightvision")) {
                int currentNV = Integer.parseInt(PythonProxy.globalMap.getOrDefault("night_vision", "0"));
                PythonProxy.globalMap.put("night_vision", String.valueOf(1 - currentNV));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentNV == 0) {
                        player.sendMessage(Text.method_30163("Night vision is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Night vision is off."), false);
                    }
                }

                MinecraftClient.getInstance().worldRenderer.reload();

                info.cancel();
                return;
            }

            if (msg.startsWith("/autoattack")) {
                int currentAutoAttack = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoattack", "0"));
                PythonProxy.globalMap.put("autoattack", String.valueOf(1 - currentAutoAttack));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoAttack == 0) {
                        player.sendMessage(Text.method_30163("Auto attack is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Auto attack is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/autofeedpig")) {
                int currentAutoFeedPig = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autofeedpig", "0"));
                PythonProxy.globalMap.put("autofeedpig", String.valueOf(1 - currentAutoFeedPig));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoFeedPig == 0) {
                        player.sendMessage(Text.method_30163("Auto pig feeding is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Auto pig feeding is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/autofeedcow")) {
                int currentAutoFeedCow = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autofeedcow", "0"));
                PythonProxy.globalMap.put("autofeedcow", String.valueOf(1 - currentAutoFeedCow));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoFeedCow == 0) {
                        player.sendMessage(Text.method_30163("Auto cow feeding is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Auto cow feeding is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/automine")) {
                int currentAutoMine = Integer.parseInt(PythonProxy.globalMap.getOrDefault("automine", "0"));
                PythonProxy.globalMap.put("automine", String.valueOf(1 - currentAutoMine));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoMine == 0) {
                        player.sendMessage(Text.method_30163("Auto mining mode is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Auto mining mode is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/safefall")) {
                int currentSafeFall = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safefall", "0"));
                PythonProxy.globalMap.put("safefall", String.valueOf(1 - currentSafeFall));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentSafeFall == 0) {
                        player.sendMessage(Text.method_30163("Safe falling is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Safe falling is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/sneaking")) {
                int currentSneaking = Integer.parseInt(PythonProxy.globalMap.getOrDefault("sneaking", "0"));
                PythonProxy.globalMap.put("sneaking", String.valueOf(1 - currentSneaking));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentSneaking == 0) {
                        player.sendMessage(Text.method_30163("Sneaking is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Sneaking is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/easyrow")) {
                int currentRowing = Integer.parseInt(PythonProxy.globalMap.getOrDefault("easyrow", "0"));
                PythonProxy.globalMap.put("easyrow", String.valueOf(1 - currentRowing));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentRowing == 0) {
                        player.sendMessage(Text.method_30163("Easy rowing is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Easy rowing is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/mobglow")) {
                int currentMobGlowing = Integer.parseInt(PythonProxy.globalMap.getOrDefault("mobglow", "0"));
                PythonProxy.globalMap.put("mobglow", String.valueOf(1 - currentMobGlowing));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentMobGlowing == 0) {
                        player.sendMessage(Text.method_30163("Mob glowing is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Mob glowing is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if (msg.startsWith("/safemine")) {
                int isSafeMine = Integer.parseInt(PythonProxy.globalMap.getOrDefault("safemine", "0"));
                PythonProxy.globalMap.put("safemine", String.valueOf(1 - isSafeMine));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (isSafeMine == 0) {
                        player.sendMessage(Text.method_30163("Safe Mining is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Safe Mining is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/autotorch")) {
                int currentAutoTorch = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autotorch", "0"));
                PythonProxy.globalMap.put("autotorch", String.valueOf(1 - currentAutoTorch));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoTorch == 0) {
                        player.sendMessage(Text.method_30163("Autotorch is on."), false);
                    }else{
                        player.sendMessage(Text.method_30163("Autotorch is off."), false);
                    }
                }

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

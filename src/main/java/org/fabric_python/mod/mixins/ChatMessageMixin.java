package org.fabric_python.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.Text;
import org.fabric_python.mod.PythonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.Scanner;

@Mixin(ClientPlayerEntity.class)
public class ChatMessageMixin {
    @Inject(at = @At("HEAD"), method = "sendChatMessage (Ljava/lang/String;)V", cancellable = true)
    protected void chatListener(String msg, CallbackInfo info) {
        if (PythonProxy.globalMap != null) {
            if (msg.startsWith("/xray")) {
                if(msg.startsWith("/xray add ")){
                    String name = msg.replace("/xray add ", "");

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (!name.isEmpty()) {
                            if (PythonProxy.noRenderList.contains(name)) {
                                player.sendMessage(Text.of("Already exists"), true);
                            } else {
                                PythonProxy.noRenderList.add(name);
                                player.sendMessage(Text.of("Added to Xray"), true);
                            }
                        }
                    }
                    info.cancel();
                    return;
                }else if(msg.startsWith("/xray del ")){
                    String name = msg.replace("/xray del ", "");

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (!name.isEmpty()) {
                            if (PythonProxy.noRenderList.contains(name)) {
                                PythonProxy.noRenderList.remove(name);
                                player.sendMessage(Text.of("Removed from Xray"), true);
                            } else {
                                player.sendMessage(Text.of("Does not exist"), true);
                            }
                        }
                    }
                    info.cancel();
                    return;
                }else {
                    int currentXray = Integer.parseInt(PythonProxy.globalMap.getOrDefault("xray", "0"));
                    PythonProxy.globalMap.put("xray", String.valueOf(1 - currentXray));

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (currentXray == 0) {
                            player.sendMessage(Text.of("Xray is on."), false);
                        } else {
                            player.sendMessage(Text.of("Xray is off."), false);
                        }
                    }

                    MinecraftClient.getInstance().worldRenderer.reload();

                    info.cancel();
                    return;
                }
            }

            if (msg.startsWith("/nightvision")) {
                int currentNV = Integer.parseInt(PythonProxy.globalMap.getOrDefault("night_vision", "0"));
                PythonProxy.globalMap.put("night_vision", String.valueOf(1 - currentNV));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentNV == 0) {
                        player.sendMessage(Text.of("Night vision is on."), false);
                    }else{
                        player.sendMessage(Text.of("Night vision is off."), false);
                    }
                }

                MinecraftClient.getInstance().worldRenderer.reload();

                info.cancel();
                return;
            }

            if(msg.startsWith("/pos1")){
                boolean flag = true;
                int x = 0, y = 0, z = 0;

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if(msg.startsWith("/pos1 reset")) {
                    PythonProxy.globalMap.put("pos1", "False");
                    info.cancel();
                    return;
                }

                if(msg.startsWith("/pos1 here")) {
                    if (player != null) {
                        x = player.getBlockPos().getX();
                        y = player.getBlockPos().getY();
                        z = player.getBlockPos().getZ();
                    } else {
                        flag = false;
                    }
                }else {
                    Scanner scanner = new Scanner(msg.replace("/pos1", "").trim());

                    if (scanner.hasNextInt()) {
                        x = scanner.nextInt();
                    } else {
                        flag = false;
                    }

                    if (flag && scanner.hasNextInt()) {
                        y = scanner.nextInt();
                    } else {
                        flag = false;
                    }

                    if (flag && scanner.hasNextInt()) {
                        z = scanner.nextInt();
                    } else {
                        flag = false;
                    }
                }

                if(flag){
                    PythonProxy.globalMap.put("pos1", "True");
                    PythonProxy.globalMap.put("pos1_x", String.valueOf(x));
                    PythonProxy.globalMap.put("pos1_y", String.valueOf(y));
                    PythonProxy.globalMap.put("pos1_z", String.valueOf(z));
                }else{
                    boolean pos1Set = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("pos1", "False"));
                    String xStr = PythonProxy.globalMap.getOrDefault("pos1_x", "0");
                    String yStr = PythonProxy.globalMap.getOrDefault("pos1_y", "0");
                    String zStr = PythonProxy.globalMap.getOrDefault("pos1_z", "0");

                    if(player != null && pos1Set) {
                        player.sendMessage(Text.of(String.format("Position 1: %s %s %s", xStr, yStr, zStr)), false);
                    }
                }

                info.cancel();
            }

            if(msg.startsWith("/pos2")){
                boolean flag = true;
                int x = 0, y = 0, z = 0;

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if(msg.startsWith("/pos2 reset")) {
                    PythonProxy.globalMap.put("pos2", "False");
                    info.cancel();
                    return;
                }

                if(msg.startsWith("/pos2 here")) {
                    if (player != null) {
                        x = player.getBlockPos().getX();
                        y = player.getBlockPos().getY();
                        z = player.getBlockPos().getZ();
                    } else {
                        flag = false;
                    }
                }else {
                    Scanner scanner = new Scanner(msg.replace("/pos2", "").trim());


                    if (scanner.hasNextInt()) {
                        x = scanner.nextInt();
                    } else {
                        flag = false;
                    }

                    if (flag && scanner.hasNextInt()) {
                        y = scanner.nextInt();
                    } else {
                        flag = false;
                    }

                    if (flag && scanner.hasNextInt()) {
                        z = scanner.nextInt();
                    } else {
                        flag = false;
                    }
                }

                if(flag){
                    PythonProxy.globalMap.put("pos2", "True");
                    PythonProxy.globalMap.put("pos2_x", String.valueOf(x));
                    PythonProxy.globalMap.put("pos2_y", String.valueOf(y));
                    PythonProxy.globalMap.put("pos2_z", String.valueOf(z));
                }else{
                    boolean pos2Set = Boolean.parseBoolean(PythonProxy.globalMap.getOrDefault("pos2", "False"));
                    String xStr = PythonProxy.globalMap.getOrDefault("pos2_x", "0");
                    String yStr = PythonProxy.globalMap.getOrDefault("pos2_y", "0");
                    String zStr = PythonProxy.globalMap.getOrDefault("pos2_z", "0");

                    if(player != null && pos2Set) {
                        player.sendMessage(Text.of(String.format("Position 2: %s %s %s", xStr, yStr, zStr)), false);
                    }
                }

                info.cancel();
            }

            if(msg.startsWith("/autobuild")) {
                int currentAutoBuild = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autobuild", "0"));
                PythonProxy.globalMap.put("autobuild", String.valueOf(1 - currentAutoBuild));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoBuild == 0) {
                        player.sendMessage(Text.of("Autobuild is on."), false);
                    }else{
                        player.sendMessage(Text.of("Autobuild is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/speed")) {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if(player != null) {
                    if (msg.startsWith("/speed 2")) {
                        PythonProxy.globalMap.put("speed", "2");
                    } else if (msg.startsWith("/speed 1")) {
                        PythonProxy.globalMap.put("speed", "1");
                    } else if (msg.startsWith("/speed 0")) {
                        PythonProxy.globalMap.put("speed", "0");
                    } else {
                        int currentSpeed = Integer.parseInt(PythonProxy.globalMap.getOrDefault("speed", "0"));
                        if (currentSpeed == 0) {
                            PythonProxy.globalMap.put("speed", "1");
                        } else {
                            PythonProxy.globalMap.put("speed", "0");
                        }

                        if (currentSpeed == 0) {
                            player.sendMessage(Text.of("Speed is on."), false);
                        } else {
                            player.sendMessage(Text.of("Speed is off."), false);
                        }
                    }
                }
                info.cancel();
                return;
            }

            if(msg.startsWith("/haste")) {
                if(msg.startsWith("/haste 2")){
                    PythonProxy.globalMap.put("haste", "2");
                }else if(msg.startsWith("/haste 1")){
                    PythonProxy.globalMap.put("haste", "1");
                }else if(msg.startsWith("/haste 0")){
                    PythonProxy.globalMap.put("haste", "0");
                }else {
                    int currentHaste = Integer.parseInt(PythonProxy.globalMap.getOrDefault("haste", "0"));
                    if(currentHaste == 0){
                        PythonProxy.globalMap.put("haste", "1");
                    }else{
                        PythonProxy.globalMap.put("haste", "0");
                    }

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (currentHaste == 0) {
                            player.sendMessage(Text.of("Haste is on."), false);
                        } else {
                            player.sendMessage(Text.of("Haste is off."), false);
                        }
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/jumpboost")) {
                if(msg.startsWith("/jumpboost 2")){
                    PythonProxy.globalMap.put("jumpboost", "2");
                }else if(msg.startsWith("/jumpboost 1")){
                    PythonProxy.globalMap.put("jumpboost", "1");
                }else if(msg.startsWith("/jumpboost 0")){
                    PythonProxy.globalMap.put("jumpboost", "0");
                }else {
                    int currentJumpBoost = Integer.parseInt(PythonProxy.globalMap.getOrDefault("jumpboost", "0"));
                    if(currentJumpBoost == 0){
                        PythonProxy.globalMap.put("jumpboost", "1");
                    }else{
                        PythonProxy.globalMap.put("jumpboost", "0");
                    }

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (currentJumpBoost == 0) {
                            player.sendMessage(Text.of("Jumpboost is on."), false);
                        } else {
                            player.sendMessage(Text.of("Jumpboost is off."), false);
                        }
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/leviation")) {
                if(msg.startsWith("/leviation 2")){
                    PythonProxy.globalMap.put("leviation", "2");
                }else if(msg.startsWith("/leviation 1")){
                    PythonProxy.globalMap.put("leviation", "1");
                }else if(msg.startsWith("/leviation 0")){
                    PythonProxy.globalMap.put("leviation", "0");
                }else {
                    int currentLeviation = Integer.parseInt(PythonProxy.globalMap.getOrDefault("leviation", "0"));
                    if(currentLeviation == 0){
                        PythonProxy.globalMap.put("leviation", "1");
                    }else{
                        PythonProxy.globalMap.put("leviation", "0");
                    }

                    MinecraftClient client = MinecraftClient.getInstance();
                    ClientPlayerEntity player = client.player;

                    if (player != null) {
                        if (currentLeviation == 0) {
                            player.sendMessage(Text.of("Leviation is on."), false);
                        } else {
                            player.sendMessage(Text.of("Leviation is off."), false);
                        }
                    }
                }

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
                        player.sendMessage(Text.of("Auto attack is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto attack is off."), false);
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
                        player.sendMessage(Text.of("Auto pig feeding is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto pig feeding is off."), false);
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
                        player.sendMessage(Text.of("Auto cow feeding is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto cow feeding is off."), false);
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
                        player.sendMessage(Text.of("Auto mining mode is on."), false);
                    }else{
                        player.sendMessage(Text.of("Auto mining mode is off."), false);
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
                        player.sendMessage(Text.of("Safe falling is on."), false);
                    }else{
                        player.sendMessage(Text.of("Safe falling is off."), false);
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
                        player.sendMessage(Text.of("Sneaking is on."), false);
                    }else{
                        player.sendMessage(Text.of("Sneaking is off."), false);
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
                        player.sendMessage(Text.of("Easy rowing is on."), false);
                    }else{
                        player.sendMessage(Text.of("Easy rowing is off."), false);
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
                        player.sendMessage(Text.of("Mob glowing is on."), false);
                    }else{
                        player.sendMessage(Text.of("Mob glowing is off."), false);
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
                        player.sendMessage(Text.of("Safe Mining is on."), false);
                    }else{
                        player.sendMessage(Text.of("Safe Mining is off."), false);
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
                        player.sendMessage(Text.of("Autotorch is on."), false);
                    }else{
                        player.sendMessage(Text.of("Autotorch is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/autolava")) {
                int currentAutoLava = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autolava", "0"));
                PythonProxy.globalMap.put("autolava", String.valueOf(1 - currentAutoLava));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoLava == 0) {
                        player.sendMessage(Text.of("Autolava is on."), false);
                    }else{
                        player.sendMessage(Text.of("Autolava is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/autoplant")) {
                int currentAutoPlant = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autoplant", "0"));
                PythonProxy.globalMap.put("autoplant", String.valueOf(1 - currentAutoPlant));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoPlant == 0) {
                        player.sendMessage(Text.of("Autoplant is on."), false);
                    }else{
                        player.sendMessage(Text.of("Autoplant is off."), false);
                    }
                }

                info.cancel();
                return;
            }

            if(msg.startsWith("/autolantern")) {
                int currentAutoLantern = Integer.parseInt(PythonProxy.globalMap.getOrDefault("autolantern", "0"));
                PythonProxy.globalMap.put("autolantern", String.valueOf(1 - currentAutoLantern));

                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (currentAutoLantern == 0) {
                        player.sendMessage(Text.of("Autolantern is on."), false);
                    }else{
                        player.sendMessage(Text.of("Autolantern is off."), false);
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
                    player.sendMessage(Text.of("You are still in the study mode."), false);
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
                    player.sendMessage(Text.of("Minutes are not a positive number."), false);
                    info.cancel();
                    return;
                }

                PythonProxy.globalMap.put("study_locked_until", String.valueOf(Instant.now().getEpochSecond() + minute * 60));
                PythonProxy.globalMap.put("study_locked", "True");

                player.sendMessage(Text.of("Study mode is on."), false);
                info.cancel();
            }
        }
    }
}

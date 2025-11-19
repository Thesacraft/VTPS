package com.damir00109;

import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarTPS {
    private static final Map<UUID, ServerBossBar> playerBossBars = new HashMap<>();
    private static final Map<UUID, Boolean> playerBossBarStates = new HashMap<>();

    /**
     * Toggles the Boss Bar for a specific player.
     */
    public static void toggleBossBar(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        boolean isEnabled = playerBossBarStates.getOrDefault(playerId, false);

        if (isEnabled) {
            stopBossBarUpdates(player); // Disable Boss Bar
            player.sendMessage(Text.of("TPS BossBar disabled"), false);
        } else {
            startBossBarUpdates(player); // Enable Boss Bar
            player.sendMessage(Text.of("TPS BossBar enabled"), false);
        }

        playerBossBarStates.put(playerId, !isEnabled); // Update state
    }

    /**
     * Starts updating the Boss Bar for the player.
     */
    private static void startBossBarUpdates(ServerPlayerEntity player) {
        ServerBossBar bossBar = new ServerBossBar(
                Text.literal("TPS: 0.00, MSPT: 0.00ms, Ping: 0ms"),
                ServerBossBar.Color.GREEN, // Initial color
                ServerBossBar.Style.PROGRESS
        );

        bossBar.addPlayer(player); // Show Boss Bar to the player
        playerBossBars.put(player.getUuid(), bossBar); // Save Boss Bar

        // Start a thread to update the Boss Bar
        new Thread(() -> {
            while (playerBossBarStates.getOrDefault(player.getUuid(), false)) {
                updateBossBar(player); // Update Boss Bar
                try {
                    Thread.sleep(500); // 500ms delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Stops updating the Boss Bar for the player.
     */
    private static void stopBossBarUpdates(ServerPlayerEntity player) {
        ServerBossBar bossBar = playerBossBars.get(player.getUuid());
        if (bossBar != null) {
            bossBar.removePlayer(player); // Hide Boss Bar
            playerBossBars.remove(player.getUuid()); // Remove Boss Bar
        }
    }

    /**
     * Updates the text and color of the Boss Bar for the player.
     */
    private static void updateBossBar(ServerPlayerEntity player) {
        ServerBossBar bossBar = playerBossBars.get(player.getUuid());
        if (bossBar == null) return;

        double tps = VanillaTPS.getCurrentTPS();
        double mspt = VanillaTPS.getCurrentMSPT();
        int ping = player.networkHandler.getLatency(); // Player's ping

        // Format text for Boss Bar
        String message = String.format("TPS: %.2f, MSPT: %.2fms, Ping: %dms", tps, mspt, ping);

        // Update Boss Bar text and color
        bossBar.setName(Text.literal(message)); // Set text
        bossBar.setColor(getBossBarColor(tps)); // Set color
    }

    /**
     * Returns the Boss Bar color based on the TPS value.
     */
    private static ServerBossBar.Color getBossBarColor(double tps) {
        if (tps >= 18.0) {
            return ServerBossBar.Color.GREEN; // All good
        } else if (tps >= 15.0) {
            return ServerBossBar.Color.YELLOW; // Medium load
        } else {
            return ServerBossBar.Color.RED; // High load
        }
    }
}
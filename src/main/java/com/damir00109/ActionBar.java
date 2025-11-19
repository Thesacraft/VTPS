package com.damir00109;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBar {
    private static final Map<UUID, Boolean> playerActionBarStates = new HashMap<>();

    /**
     * Checks if the Action Bar is enabled for the player.
     */
    public static boolean isActionBarEnabled(ServerPlayerEntity player) {
        return playerActionBarStates.getOrDefault(player.getUuid(), false);
    }

    /**
     * Toggles the Action Bar for a specific player.
     */
    public static void toggleActionBar(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        boolean isEnabled = playerActionBarStates.getOrDefault(playerId, false);

        if (isEnabled) {
            stopActionBarUpdates(player); // Disable Action Bar
            player.sendMessage(Text.of("TPS ActionBar disabled"), false);
        } else {
            startActionBarUpdates(player); // Enable Action Bar
            player.sendMessage(Text.of("TPS ActionBar enabled"), false);
        }

        playerActionBarStates.put(playerId, !isEnabled); // Update state
    }

    /**
     * Starts updating the Action Bar for the player.
     */
    private static void startActionBarUpdates(ServerPlayerEntity player) {
        // Start a thread to update the Action Bar
        new Thread(() -> {
            while (playerActionBarStates.getOrDefault(player.getUuid(), false)) {
                updateActionBar(player); // Update Action Bar
                try {
                    Thread.sleep(500); // 500 ms delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Stops updating the Action Bar for the player.
     */
    private static void stopActionBarUpdates(ServerPlayerEntity player) {
        // Nothing needs to be done, the thread will terminate itself
    }

    /**
     * Updates the Action Bar text for the player.
     */
    public static void updateActionBar(ServerPlayerEntity player) {
        double tps = VanillaTPS.getCurrentTPS();
        double mspt = VanillaTPS.getCurrentMSPT();
        int ping = player.networkHandler.getLatency(); // Player's ping

        // Format the text for the Action Bar
        String message = String.format("TPS: %.2f, MSPT: %.2fms, Ping: %dms", tps, mspt, ping);

        // Send the message to the Action Bar
        player.sendMessage(Text.of(message), true);
    }
}
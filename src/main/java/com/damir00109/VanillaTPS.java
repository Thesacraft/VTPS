package com.damir00109;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;
import java.util.Queue;
import com.mojang.brigadier.CommandDispatcher;

import static net.minecraft.server.command.CommandManager.literal;

public class VanillaTPS {
	public static final String MOD_ID = "vanilla-tps";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// General fields (now public)
	public static long lastTickTime = 0;
	public static double tps = 20.0;
	public static double mspt = 50.0;
	public static final Queue<Double> tps5s = new ArrayDeque<>(100);
	public static final Queue<Double> tps1m = new ArrayDeque<>(1200);
	public static final Queue<Double> tps5m = new ArrayDeque<>(6000);
	public static final Queue<Double> tps15m = new ArrayDeque<>(18000);
	public static final Queue<Double> mspt5s = new ArrayDeque<>(100);
	public static final Queue<Double> mspt1m = new ArrayDeque<>(1200);
	public static final Queue<Double> mspt5m = new ArrayDeque<>(6000);
	public static final Queue<Double> mspt15m = new ArrayDeque<>(18000);

	/**
	 * Method for processing server ticks.
	 */
	public static void onServerTick(MinecraftServer server) {
		long currentTime = System.nanoTime();
		if (lastTickTime != 0) {
			double deltaTime = (currentTime - lastTickTime) / 1_000_000_000.0;
			tps = 1.0 / deltaTime;
			mspt = deltaTime * 1000;

			collectStatistics(tps, mspt);
		}
		lastTickTime = currentTime;
	}

	/**
	 * Method for collecting TPS and MSPT statistics.
	 */
	private static void collectStatistics(double tps, double mspt) {
		tps5s.add(tps);
		tps1m.add(tps);
		tps5m.add(tps);
		tps15m.add(tps);

		mspt5s.add(mspt);
		mspt1m.add(mspt);
		mspt5m.add(mspt);
		mspt15m.add(mspt);

		if (tps5s.size() > 100) tps5s.poll();
		if (tps1m.size() > 1200) tps1m.poll();
		if (tps5m.size() > 6000) tps5m.poll();
		if (tps15m.size() > 18000) tps15m.poll();

		if (mspt5s.size() > 100) mspt5s.poll();
		if (mspt1m.size() > 1200) mspt1m.poll();
		if (mspt5m.size() > 6000) mspt5m.poll();
		if (mspt15m.size() > 18000) mspt15m.poll();
	}

	/**
	 * Returns the current TPS value.
	 */
	public static double getCurrentTPS() {
		return tps;
	}

	/**
	 * Returns the current MSPT value.
	 */
	public static double getCurrentMSPT() {
		return mspt;
	}

	/**
	 * Method for getting CPU information.
	 */
	public static double getCpuUsage() {
		com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		double processLoad = osBean.getProcessCpuLoad() * 100;
		return Double.isNaN(processLoad) ? 0 : processLoad;
	}

	/**
	 * Method for getting RAM information in percentage.
	 */
	public static double getRamUsagePercentage() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		long maxMemory = runtime.maxMemory();
		return (double) usedMemory / maxMemory * 100;
	}

	/**
	 * Method for getting RAM information in "Used: XM / YM (max: ZM)" format.
	 */
	public static String getRamUsageFormatted() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		long maxMemory = runtime.maxMemory();
		return String.format("%dM / %dM (max: %dM)",
				usedMemory / 1024 / 1024,
				totalMemory / 1024 / 1024,
				maxMemory / 1024 / 1024);
	}

	/**
	 * Method for registering commands.
	 */
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		// /tps command
		dispatcher.register(
				literal("tps")
						.requires(source -> source.hasPermissionLevel(2)) // Requires permission level 2 (operator)
						.executes(context -> {
							context.getSource().sendMessage(Text.of(TPS.getTpsInfo()));
							return 1;
						})
		);

		// /tps-actionbar command
		dispatcher.register(
				literal("tps-actionbar")
						.requires(source -> source.hasPermissionLevel(2)) // Requires permission level 2 (operator)
						.executes(context -> {
							ServerPlayerEntity player = context.getSource().getPlayer();
							if (player != null) {
								ActionBar.toggleActionBar(player);
							}
							return 1;
						})
		);

		// /tabtps command
		dispatcher.register(
				literal("tabtps")
						.requires(source -> source.hasPermissionLevel(2)) // Requires permission level 2 (operator)
						.executes(context -> {
							ServerPlayerEntity player = context.getSource().getPlayer();
							if (player != null) {
								BossBarTPS.toggleBossBar(player);
							}
							return 1;
						})
		);
	}
}
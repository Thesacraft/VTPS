package com.damir00109;

import java.util.Queue;

public class TPS {
    /**
     * Method to get TPS information.
     */
    public static String getTpsInfo() {
        StringBuilder builder = new StringBuilder();

        // Headers
        builder.append(String.format("%-15s", "TPS:"))
                .append(String.format("%-15s", "MSPT:"))
                .append(String.format("%-20s", "Resources:"))
                .append("\n");

        // Collect all TPS values to determine the maximum length
        double tps5sValue = getAverage(VanillaTPS.tps5s);
        double tps1mValue = getAverage(VanillaTPS.tps1m);
        double tps5mValue = getAverage(VanillaTPS.tps5m);
        double tps15mValue = getAverage(VanillaTPS.tps15m);

        // Convert TPS values to strings
        String tps5sStr = String.format("%.2f", tps5sValue);
        String tps1mStr = String.format("%.2f", tps1mValue);
        String tps5mStr = String.format("%.2f", tps5mValue);
        String tps15mStr = String.format("%.2f", tps15mValue);

        // Find the maximum length of the TPS string
        int maxTpsLength = Math.max(Math.max(tps5sStr.length(), tps1mStr.length()),
                Math.max(tps5mStr.length(), tps15mStr.length()));

        // 5 seconds
        builder.append("├─ 5s: ")
                .append(String.format("%-" + maxTpsLength + "s", tps5sStr))
                .append("   ├─ 5s: ")
                .append(String.format("%-10.2f", getAverage(VanillaTPS.mspt5s)))
                .append("   CPU: ").append(String.format("%.2f%%", VanillaTPS.getCpuUsage()))
                .append("\n");

        // 1 minute
        builder.append("├─ 1m: ")
                .append(String.format("%-" + maxTpsLength + "s", tps1mStr))
                .append("   ├─ 1m: ")
                .append(String.format("%-10.2f", getAverage(VanillaTPS.mspt1m)))
                .append("   RAM: ").append(String.format("%.2f%%", VanillaTPS.getRamUsagePercentage()))
                .append("\n");

        // 5 minutes
        builder.append("├─ 5m: ")
                .append(String.format("%-" + maxTpsLength + "s", tps5mStr))
                .append("   ├─ 5m: ")
                .append(String.format("%-10.2f", getAverage(VanillaTPS.mspt5m)))
                .append("\n");

        // 15 minutes
        builder.append("└─ 15m: ")
                .append(String.format("%-" + maxTpsLength + "s", tps15mStr))
                .append("  └─ 15m: ")
                .append(String.format("%-10.2f", getAverage(VanillaTPS.mspt15m)))
                .append("\n");

        // Memory information (separate line)
        builder.append("\nUsed: ").append(VanillaTPS.getRamUsageFormatted()).append("\n");

        return builder.toString();
    }

    /**
     * Method to calculate the average value.
     */
    private static double getAverage(Queue<Double> queue) {
        if (queue.isEmpty()) return 0;
        double sum = 0;
        for (double value : queue) {
            sum += value;
        }
        return sum / queue.size();
    }
}
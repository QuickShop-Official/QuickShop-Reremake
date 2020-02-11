package org.maxgamer.quickshop.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public final class NmsUtils {

    /**
     * Get the MinecraftServer class
     *
     * @param className custom class name
     * @return The class
     * @throws ClassNotFoundException the exception if cannot found target class.
     */
    @NotNull
    public static Optional<Class<?>> getNMSClass(@Nullable String className)
        throws ClassNotFoundException {
        if (className == null) {
            className = "MinecraftServer";
        }
        final String name = Bukkit.getServer().getClass().getPackage().getName();
        final String version = name.substring(name.lastIndexOf('.') + 1);
        return Optional.of(Class.forName("net.minecraft.server." + version + "." + className));
    }

    /**
     * Get the net.minecraft.server version.
     * E.g v1_15_R1
     *
     * @return The version of NMS, return "Unknown" when failed (usually is impossible)
     */
    @NotNull
    public static String getNMSVersion() {
        final @NotNull String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Get MinecraftServer's TPS
     *
     * @return TPS (e.g 19.92)
     */
    public static double getTPS() {
        Field tpsField;
        Object serverInstance;
        try {
            Optional<Class<?>> nmsClass = getNMSClass("MinecraftServer");
            if (nmsClass.isPresent()) {
                serverInstance = nmsClass.get().getMethod("getServer").invoke(null);
                tpsField = serverInstance.getClass().getField("recentTps");
            } else {
                return 20.0;
            }
        } catch (NullPointerException | NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return 20.0;
        }
        try {
            final double[] tps = ((double[]) tpsField.get(serverInstance));
            return tps[0];
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 20.0;
        }
    }

    /**
     * Get Minecraft server version
     *
     * @return The version like 1.15.2 1.14.4
     */
    public static String getServerVersion() {
        try {
            Field consoleField = Bukkit.getServer().getClass().getDeclaredField("console");
            consoleField.setAccessible(true); // protected
            Object console = consoleField.get(Bukkit.getServer()); // dedicated server
            return String.valueOf(
                console.getClass().getSuperclass().getMethod("getVersion").invoke(console));
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
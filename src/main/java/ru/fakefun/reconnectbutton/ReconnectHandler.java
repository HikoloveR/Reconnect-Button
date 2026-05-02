package ru.fakefun.reconnectbutton;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public final class ReconnectHandler {
    private static ServerInfo lastServer;
    private static boolean reconnectInProgress;

    private ReconnectHandler() {
    }

    public static void setLastServer(ServerInfo serverInfo) {
        lastServer = copyServerInfo(serverInfo);
    }

    public static boolean hasServer(MinecraftClient client) {
        return getServer(client) != null;
    }

    public static boolean isReconnectInProgress() {
        return reconnectInProgress;
    }

    public static void clearReconnectInProgress() {
        reconnectInProgress = false;
    }

    public static void reconnect(MinecraftClient client, Screen parent) {
        ServerInfo serverInfo = getServer(client);
        if (serverInfo == null || reconnectInProgress) {
            return;
        }

        reconnectInProgress = true;
        ServerAddress address = ServerAddress.parse(serverInfo.address);
        ServerInfo serverInfoCopy = copyServerInfo(serverInfo);
        client.execute(() -> ConnectScreen.connect(parent, client, address, serverInfoCopy, true, null));
    }

    private static ServerInfo getServer(MinecraftClient client) {
        ServerInfo currentServer = client.getCurrentServerEntry();
        return currentServer != null ? currentServer : lastServer;
    }

    private static ServerInfo copyServerInfo(ServerInfo serverInfo) {
        ServerInfo copy = new ServerInfo(serverInfo.name, serverInfo.address, serverInfo.getServerType());
        copy.copyWithSettingsFrom(serverInfo);
        return copy;
    }
}

package ru.fakefun.reconnectbutton.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.fakefun.reconnectbutton.ReconnectHandler;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
    @Inject(method = "connect", at = @At("HEAD"))
    private static void reconnectButton$rememberServer(Screen parent, MinecraftClient client, ServerAddress address,
                                                       ServerInfo info, boolean quickPlay,
                                                       CookieStorage cookieStorage, CallbackInfo ci) {
        if (info != null && info.address != null && !info.address.isBlank()) {
            ReconnectHandler.setLastServer(info);
        }
    }
}

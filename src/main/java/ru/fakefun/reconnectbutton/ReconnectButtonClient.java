package ru.fakefun.reconnectbutton;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import ru.fakefun.reconnectbutton.mixin.DisconnectedScreenAccessor;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ReconnectButtonClient implements ClientModInitializer {
    private static final Text RECONNECT_TEXT = Text.translatable("reconnect_button.button.reconnect");
    private static final int VANILLA_BUTTON_GAP = 4;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ReconnectHandler.clearReconnectInProgress();

            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (serverInfo != null && !client.isInSingleplayer()) {
                ReconnectHandler.setLastServer(serverInfo);
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof DisconnectedScreen) || !ReconnectHandler.hasServer(client)) {
                return;
            }

            ReconnectHandler.clearReconnectInProgress();
            addReconnectButtonUnderBackButton(screen, scaledWidth, scaledHeight);
        });
    }

    private static void addReconnectButtonUnderBackButton(Screen screen, int scaledWidth, int scaledHeight) {
        List<ClickableWidget> buttons = Screens.getButtons(screen);
        if (buttons.stream().anyMatch(button -> isReconnectButton(button.getMessage()))) {
            return;
        }

        Optional<ClickableWidget> backButton = buttons.stream()
                .filter(button -> isBackToServersButton(button.getMessage()))
                .findFirst()
                .or(() -> buttons.stream().filter(button -> !isReconnectButton(button.getMessage())).findFirst());

        int width = backButton.map(ClickableWidget::getWidth).orElse(200);
        int height = backButton.map(ClickableWidget::getHeight).orElse(20);
        int x = backButton.map(ClickableWidget::getX).orElse(scaledWidth / 2 - width / 2);
        int y = backButton.map(button -> button.getY() + button.getHeight() + VANILLA_BUTTON_GAP)
                .orElse(scaledHeight / 2 + 56);

        ButtonWidget button = ButtonWidget.builder(RECONNECT_TEXT, buttonWidget -> reconnect(screen, buttonWidget))
                .dimensions(x, y, width, height)
                .build();
        button.active = !ReconnectHandler.isReconnectInProgress();
        buttons.add(button);
    }

    private static boolean isBackToServersButton(Text message) {
        if (message.getContent() instanceof TranslatableTextContent content) {
            String key = content.getKey();
            if ("gui.toMenu".equals(key) || "gui.back".equals(key)) {
                return true;
            }
        }

        String label = message.getString().toLowerCase(Locale.ROOT);
        return label.contains("\u043a \u0441\u043f\u0438\u0441\u043a\u0443 \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432")
                || label.contains("\u0441\u043f\u0438\u0441\u043a\u0443 \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432")
                || label.contains("server list")
                || label.contains("multiplayer")
                || label.contains("main menu");
    }

    private static boolean isReconnectButton(Text message) {
        String label = message.getString().toLowerCase(Locale.ROOT);
        return message.equals(RECONNECT_TEXT)
                || label.contains("reconnect")
                || label.contains("\u043f\u0435\u0440\u0435\u043f\u043e\u0434\u043a\u043b\u044e\u0447");
    }

    private static void reconnect(Screen currentScreen, ButtonWidget button) {
        button.active = false;
        MinecraftClient client = MinecraftClient.getInstance();
        ReconnectHandler.reconnect(client, getReconnectParent(currentScreen));
    }

    private static Screen getReconnectParent(Screen screen) {
        Screen parent = screen;
        for (int i = 0; i < 8 && parent instanceof DisconnectedScreenAccessor accessor; i++) {
            Screen next = accessor.reconnectButton$getParent();
            if (next == null || next == parent) {
                break;
            }
            parent = next;
        }
        return parent;
    }
}

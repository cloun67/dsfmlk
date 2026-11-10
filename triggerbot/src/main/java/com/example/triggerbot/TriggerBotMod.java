package com.example.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TriggerBotMod implements ClientModInitializer {
    public static KeyBinding toggleKey;
    public static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        TriggerBotConfig.load();

        // Appears in Options -> Controls -> Key Binds, can be rebound to any key
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.triggerbot.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.triggerbot"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                TriggerBot.reset();
                if (client.player != null && TriggerBotConfig.INSTANCE.showMessages) {
                    client.player.sendMessage(
                            Text.translatable(enabled ? "msg.triggerbot.on" : "msg.triggerbot.off"), true);
                }
            }
            if (enabled) TriggerBot.tick(client);
        });
    }
}

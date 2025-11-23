package fr.iglee42.compressed.fabric.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import fr.iglee42.compressed.client.CompressedClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class CompressedFabricClient implements ClientModInitializer {

    public static final fr.iglee42.compressed.fabric.client.CompressedClientConfig CLIENT_CONFIG = fr.iglee42.compressed.fabric.client.CompressedClientConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        CompressedClient.init();
        KeyBindingHelper.registerKeyBinding(CompressedClient.SHOW_BOX_HUD);
        CompressedClient.registerBER();
    }
}

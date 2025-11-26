package fr.iglee42.compressedbox.fabric.client;

import fr.iglee42.compressedbox.client.CompressedClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public final class CompressedFabricClient implements ClientModInitializer {

    public static final fr.iglee42.compressedbox.fabric.client.CompressedClientConfig CLIENT_CONFIG = fr.iglee42.compressedbox.fabric.client.CompressedClientConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        CompressedClient.init();
        KeyBindingHelper.registerKeyBinding(CompressedClient.SHOW_BOX_HUD);
        CompressedClient.registerBER();
    }
}

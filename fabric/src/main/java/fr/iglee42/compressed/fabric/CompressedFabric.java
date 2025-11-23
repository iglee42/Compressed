package fr.iglee42.compressed.fabric;

import fr.iglee42.compressed.registries.CBlockEntities;
import net.fabricmc.api.ModInitializer;

import fr.iglee42.compressed.Compressed;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public final class CompressedFabric implements ModInitializer {

    public static final fr.iglee42.compressed.fabric.config.CompressedConfig CONFIG = fr.iglee42.compressed.fabric.config.CompressedConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Compressed.init();

        registerPackets();

        registerCapabilities();

    }

    private void registerCapabilities() {

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (be.getBox() == null) return null;
            if (be.getBox().getItems(be.getLevel()) == null) return null;
            return InventoryStorage.of(be.getBox().getItems(be.getLevel()),direction);
        }, CBlockEntities.COMPRESSED.get());

        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (be.getBox() == null) return null;
            if (be.getHandler() == null) return null;
            return InventoryStorage.of(be.getHandler(),direction);
        }, CBlockEntities.SLOT.get());

    }

    private void registerPackets() {
        //PayloadTypeRegistry.playC2S().register(ExitPlayerFromBoxPayload.TYPE,ExitPlayerFromBoxPayload.STREAM_CODEC);
    }
}

package fr.iglee42.compressedbox.fabric;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.fabric.CreativeTabRegistryImpl;
import fr.iglee42.compressedbox.fabric.implementations.ConnectedTankStorageImpl;
import fr.iglee42.compressedbox.fabric.implementations.SimpleTankStorageImpl;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.registries.CBlocks;
import fr.iglee42.compressedbox.registries.CCreativeTabs;
import fr.iglee42.compressedbox.registries.CItems;
import net.fabricmc.api.ModInitializer;

import fr.iglee42.compressedbox.CompressedBox;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class CompressedFabric implements ModInitializer {

    public static final fr.iglee42.compressedbox.fabric.config.CompressedConfig CONFIG = fr.iglee42.compressedbox.fabric.config.CompressedConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CompressedBox.init();

        registerPackets();

        registerCapabilities();


        ItemGroupEvents.modifyEntriesEvent(CCreativeTabs.TAB.getKey()).register(content->{
            content.accept(CBlocks.COMPRESSED_BLOCK.get());
            content.accept(CBlocks.SLOT.get());
            content.accept(CBlocks.TANK.get());
            content.accept(CBlocks.CHUNK_LOADER.get());
            content.accept(CBlocks.INFINITE_CHUNK_LOADER.get());
            content.accept(CItems.WALL_PUSHER.get());
        });
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

        FluidStorage.SIDED.registerForBlockEntity((be,direction)->{
            if (be.getHandler() == null) return null;
            return new SimpleTankStorageImpl(be.getHandler());
        },CBlockEntities.TANK.get());

        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (be.getBox() == null) return null;
            if (be.getBox().getItems(be.getLevel()) == null) return null;
            return new ConnectedTankStorageImpl(be.getBox().getFluids(be.getLevel()));
        }, CBlockEntities.COMPRESSED.get());


    }

    private void registerPackets() {
        //PayloadTypeRegistry.playC2S().register(ExitPlayerFromBoxPayload.TYPE,ExitPlayerFromBoxPayload.STREAM_CODEC);
    }
}

package fr.iglee42.compressedbox.neoforge;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.neoforge.client.CompressedClientConfigNeoForge;
import fr.iglee42.compressedbox.neoforge.client.CompressedClientNeoForge;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@Mod(CompressedBox.MODID)
public final class CompressedNeoForge {
    public CompressedNeoForge(IEventBus modBus, ModContainer container) {
        // Run our common setup.
        CompressedBox.init();
        if (FMLEnvironment.dist.isClient()) CompressedClientNeoForge.init();

        modBus.addListener(this::registerCapabilities);
        container.registerConfig(ModConfig.Type.COMMON, CompressedConfigNeoForge.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, CompressedClientConfigNeoForge.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CBlockEntities.COMPRESSED.get(),
                ((be,c)->{
                    if (be.getBox() == null) return null;
                    if (be.getBox().getItems(be.getLevel()) == null) return null;
                    return new InvWrapper(be.getBox().getItems(be.getLevel()));
                })
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CBlockEntities.SLOT.get(),
                ((be,c)->{
                    if (be.getHandler() == null) return null;
                    return new InvWrapper(be.getHandler());
                })
        );
    }
}

package fr.iglee42.compressedbox.neoforge;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.neoforge.client.CompressedClientConfigNeoForge;
import fr.iglee42.compressedbox.neoforge.client.CompressedClientNeoForge;
import fr.iglee42.compressedbox.neoforge.implementations.ConnectedTankWrapper;
import fr.iglee42.compressedbox.neoforge.implementations.FluidStackListWrapper;
import fr.iglee42.compressedbox.neoforge.implementations.SimpleTankWrapper;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.minecraft.core.NonNullList;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
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
                    if (be.getLevel().getServer() == null) return new ItemStackHandler(NonNullList.copyOf(be.getClientItems()));
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

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CBlockEntities.COMPRESSED.get(),
                ((be,c)->{
                    if (be.getBox() == null) return null;
                    if (be.getLevel().getServer() == null) return new FluidStackListWrapper(be.getClientFluids());
                    if (be.getBox().getFluids(be.getLevel()) == null) return null;
                    return new ConnectedTankWrapper(be.getBox().getFluids(be.getLevel()));
                })
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CBlockEntities.TANK.get(),
                ((be,c)->{
                    if (be.getHandler() == null) return null;
                    return new SimpleTankWrapper(be.getHandler());
                })
        );
    }
}

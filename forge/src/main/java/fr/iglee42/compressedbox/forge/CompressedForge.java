package fr.iglee42.compressedbox.forge;

import dev.architectury.platform.forge.EventBuses;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.blockentities.CompressedBlockEntity;
import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.forge.client.CompressedClientConfigForge;
import fr.iglee42.compressedbox.forge.client.CompressedClientForge;
import fr.iglee42.compressedbox.forge.implementations.ConnectedTankWrapper;
import fr.iglee42.compressedbox.forge.implementations.SimpleTankWrapper;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod(CompressedBox.MODID)
public final class CompressedForge {
    public CompressedForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(CompressedBox.MODID,modBus);
        CompressedBox.init();
        if (FMLEnvironment.dist.isClient()) CompressedClientForge.init();

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::addCapabilities);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CompressedConfigForge.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CompressedClientConfigForge.SPEC);
    }

    private void addCapabilities(AttachCapabilitiesEvent<BlockEntity> event){
        if (event.getObject() instanceof CompressedBlockEntity be){
            event.addCapability(CBlockEntities.COMPRESSED.getId(), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    if (be.getBox() == null) return LazyOptional.empty();
                    if (be.getLevel() == null) return LazyOptional.empty();
                    if (capability == ForgeCapabilities.ITEM_HANDLER){
                        if (be.getBox().getItems(be.getLevel()) == null) return LazyOptional.empty();
                        return LazyOptional.of(() -> new InvWrapper( be.getBox().getItems(be.getLevel()))).cast();
                    } else if (capability == ForgeCapabilities.FLUID_HANDLER){
                        if (be.getBox().getFluids(be.getLevel()) == null) return LazyOptional.empty();
                        return LazyOptional.of(() -> new ConnectedTankWrapper( be.getBox().getFluids(be.getLevel()))).cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }

        if (event.getObject() instanceof SlotModule be){
            event.addCapability(CBlockEntities.SLOT.getId(), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    if (be.getHandler() == null) return LazyOptional.empty();
                    return capability == ForgeCapabilities.ITEM_HANDLER ? LazyOptional.of(() -> new InvWrapper( be.getHandler())).cast() : LazyOptional.empty();
                }
            });
        }

        if (event.getObject() instanceof TankModule be){
            event.addCapability(CBlockEntities.TANK.getId(), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    if (be.getHandler() == null) return LazyOptional.empty();
                    return capability == ForgeCapabilities.FLUID_HANDLER ? LazyOptional.of(() -> new SimpleTankWrapper(be.getHandler())).cast() : LazyOptional.empty();
                }
            });
        }
    }

}

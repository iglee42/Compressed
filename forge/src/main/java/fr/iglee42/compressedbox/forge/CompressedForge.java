package fr.iglee42.compressedbox.forge;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.forge.client.CompressedClientConfigForge;
import fr.iglee42.compressedbox.forge.client.CompressedClientForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CompressedBox.MODID)
public final class CompressedForge {
    public CompressedForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CompressedBox.init();
        if (FMLEnvironment.dist.isClient()) CompressedClientForge.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CompressedConfigForge.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CompressedClientConfigForge.SPEC);
    }

}

package fr.iglee42.compressedbox.registries;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressedbox.CompressedBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(CompressedBox.MODID, Registries.CREATIVE_MODE_TAB);


    public static RegistrySupplier<CreativeModeTab> TAB;

    public static void initTabs(){
        TAB = TABS.register("tab",
                () -> CreativeTabRegistry.create(Component.translatable("category.compressedbox.tab"),
                        () -> new ItemStack(CBlocks.COMPRESSED_BLOCK.get())
                )
        );
        TABS.register();
    }
}

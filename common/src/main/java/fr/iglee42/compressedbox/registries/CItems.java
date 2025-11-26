package fr.iglee42.compressedbox.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.items.WallPusherItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class CItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(CompressedBox.MODID, Registries.ITEM);

    public static final RegistrySupplier<Item> WALL_PUSHER = ITEMS.register("wall_pusher",()->new WallPusherItem(baseProps()));


    public static Item.Properties baseProps(){
        return new Item.Properties().arch$tab(CCreativeTabs.TAB);
    }

}

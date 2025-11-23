package fr.iglee42.compressed.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressed.Compressed;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class CItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Compressed.MODID, Registries.ITEM);


    public static Item.Properties baseProps(){
        return new Item.Properties();
    }

}

package fr.iglee42.compressedbox.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressedbox.CompressedBox;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.UUID;

public class CDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(CompressedBox.MODID, Registries.DATA_COMPONENT_TYPE);


    public static final RegistrySupplier<DataComponentType<UUID>> BOX_ID = DATA_COMPONENTS.register("box_id",()-> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());


}

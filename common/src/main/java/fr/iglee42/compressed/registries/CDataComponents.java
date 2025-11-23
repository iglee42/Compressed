package fr.iglee42.compressed.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.blockentities.CompressedBlockEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.UUID;
import java.util.function.Supplier;

public class CDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Compressed.MODID, Registries.DATA_COMPONENT_TYPE);


    public static final RegistrySupplier<DataComponentType<UUID>> BOX_ID = DATA_COMPONENTS.register("box_id",()-> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());


}

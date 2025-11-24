package fr.iglee42.compressed.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.blockentities.modules.SlotModule;
import fr.iglee42.compressed.blockentities.modules.ChunkLoadModule;
import fr.iglee42.compressed.blockentities.CompressedBlockEntity;
import fr.iglee42.compressed.blockentities.modules.InfiniteChunkLoadModule;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Compressed.MODID, Registries.BLOCK_ENTITY_TYPE);


    public static RegistrySupplier<BlockEntityType<CompressedBlockEntity>> COMPRESSED = register("compressed_block", CompressedBlockEntity::new, CBlocks.COMPRESSED_BLOCK::get);
    public static RegistrySupplier<BlockEntityType<SlotModule>> SLOT = register("slot", SlotModule::new, CBlocks.SLOT::get);
    public static RegistrySupplier<BlockEntityType<ChunkLoadModule>> CHUNK_LOAD = register("chunk_load", ChunkLoadModule::new, CBlocks.CHUNK_LOADER::get);
    public static RegistrySupplier<BlockEntityType<InfiniteChunkLoadModule>> INFINITE_CHUNK_LOAD = register("infinite_chunk_load", InfiniteChunkLoadModule::new, CBlocks.INFINITE_CHUNK_LOADER::get);

    private static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> builder,Supplier<Block> block){
        return BLOCK_ENTITIES.register(id,()->BlockEntityType.Builder.of(builder,block.get()).build(null));
    }

}

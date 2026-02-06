package fr.iglee42.compressedbox.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.blocks.modules.ChunkLoaderModuleBlock;
import fr.iglee42.compressedbox.blocks.CompressedBlock;
import fr.iglee42.compressedbox.blocks.modules.InfiniteChunkLoaderModuleBlock;
import fr.iglee42.compressedbox.blocks.modules.SlotModuleBlock;
import fr.iglee42.compressedbox.blocks.modules.TankModuleBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class CBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(CompressedBox.MODID, Registries.BLOCK);

    public static final RegistrySupplier<CompressedBlock> COMPRESSED_BLOCK = createBlock("compressed_block",()->new CompressedBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion()));
    public static final RegistrySupplier<SlotModuleBlock> SLOT = createBlock("slot", ()->new SlotModuleBlock(BlockBehaviour.Properties.of().strength(2)));
    public static final RegistrySupplier<TankModuleBlock> TANK = createBlock("tank", ()->new TankModuleBlock(BlockBehaviour.Properties.of().noOcclusion().strength(2)));
    public static final RegistrySupplier<ChunkLoaderModuleBlock> CHUNK_LOADER = createBlock("chunk_loader", ()->new ChunkLoaderModuleBlock(BlockBehaviour.Properties.of().strength(2).lightLevel(bs-> RespawnAnchorBlock.getScaledChargeLevel(bs,15))));
    public static final RegistrySupplier<InfiniteChunkLoaderModuleBlock> INFINITE_CHUNK_LOADER = createBlock("infinite_chunk_loader", ()->new InfiniteChunkLoaderModuleBlock(BlockBehaviour.Properties.of().strength(3).lightLevel(bs->15)));


    private static <T extends Block>RegistrySupplier<T> createBlock(String id, Supplier<T> builder){
        RegistrySupplier<T> block = createBlockWithoutItem(id,builder);
        CItems.ITEMS.register(id,()->new BlockItem(block.get(),CItems.baseProps()));
        return block;
    }

    private static <T extends Block> RegistrySupplier<T> createBlockWithoutItem(String id, Supplier<T> builder){
        return BLOCKS.register(id,builder);
    }
}

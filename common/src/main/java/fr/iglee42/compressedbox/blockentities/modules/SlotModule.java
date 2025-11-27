package fr.iglee42.compressedbox.blockentities.modules;

import fr.iglee42.compressedbox.registries.CBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;

public class SlotModule extends Module {

    @Getter
    private final SimpleContainer handler = new SimpleContainer(1);


    public SlotModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.SLOT.get(), blockPos, blockState);
    }

    @Override
    protected void save(CompoundTag tag, HolderLookup.Provider registries, boolean forClient) {
        super.save(tag, registries, forClient);
        tag.put("Inventory",handler.createTag(registries));
    }

    @Override
    protected void load(CompoundTag tag, HolderLookup.Provider registries) {
        super.load(tag, registries);
        handler.fromTag(tag.getList("Inventory", Tag.TAG_COMPOUND),registries);
    }

    @Override
    public void removed() {
        super.removed();
        Containers.dropContents(level,getBlockPos(),handler);
    }
}

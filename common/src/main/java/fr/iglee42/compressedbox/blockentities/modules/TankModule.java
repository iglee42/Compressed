package fr.iglee42.compressedbox.blockentities.modules;

import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;

public class TankModule extends Module {

    @Getter
    private final SimpleFluidContainer handler = new SimpleFluidContainer();


    public TankModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.TANK.get(), blockPos, blockState);
    }

    @Override
    protected void save(CompoundTag tag, boolean forClient) {
        super.save(tag,  forClient);
        tag.put("Tank",handler.createTag());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        handler.fromTag(tag.getCompound("Tank"));
    }

    @Override
    public void removed() {
        super.removed();
    }
}

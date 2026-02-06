package fr.iglee42.compressedbox.containers.fluids;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectedTankHandler{

    private final Level level;
    private final BlockPos minPos,maxPos ;

    public ConnectedTankHandler(Level level, BlockPos minPos, BlockPos maxPos) {
        this.level = level;
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public int getContainerSize() {
        return getBlockEntities().size();
    }
    
    public boolean isEmpty() {
        return getBlockEntities().stream().allMatch(be-> be.getHandler().isEmpty());
    }

    public FluidStack getFluid(int i) {
        List<TankModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return FluidStack.empty();
        if (blocks.get(i) == null) return FluidStack.empty();
        return blocks.get(i).getHandler().getFluid();
    }

    public FluidStack removeFluid(int i,int j) {
        List<TankModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return FluidStack.empty();
        return blocks.get(i).getHandler().removeFluid(j);
    }
    
    public FluidStack removeFluidNoUpdate(int i) {
        List<TankModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return FluidStack.empty();
        return blocks.get(i).getHandler().removeFluidNoUpdate();
    }
    
    public void setFluid(int i, FluidStack fluidStack) {
        List<TankModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return;
        if (blocks.get(i) == null) return;
        blocks.get(i).getHandler().setFluid(fluidStack);
    }
    
    public void setChanged() {
        getBlockEntities().stream().filter(Objects::nonNull).forEach(be->be.getHandler().setChanged());
    }

    public boolean stillValid(Player player) {
        return getBlockEntities().stream().allMatch(be->be.getHandler().stillValid(player));
    }
    
    public void clearContent() {
        getBlockEntities().forEach(be->be.getHandler().clearContent());
    }

    private List<TankModule> getBlockEntities(){

        List<TankModule> blocks = new ArrayList<>();
        BlockPos.betweenClosed(minPos,maxPos).forEach(bp->{
            if (level.getBlockEntity(bp) instanceof TankModule be){
                blocks.add(be);
            }
        });
        return blocks;
    }

    public List<FluidStack> getFluids(){
        return getBlockEntities().stream().map(be->be.getHandler().getFluid()).toList();
    }

}

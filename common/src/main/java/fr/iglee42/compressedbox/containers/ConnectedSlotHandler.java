package fr.iglee42.compressedbox.containers;

import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectedSlotHandler implements Container {

    private final Level level;
    private final BlockPos minPos,maxPos ;

    public ConnectedSlotHandler(Level level, BlockPos minPos, BlockPos maxPos) {
        this.level = level;
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    @Override
    public int getContainerSize() {
        return getBlockEntities().size();
    }

    @Override
    public boolean isEmpty() {
        return getBlockEntities().stream().allMatch(be-> be.getHandler().isEmpty());
    }

    @Override
    public ItemStack getItem(int i) {
        List<SlotModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return ItemStack.EMPTY;
        if (blocks.get(i) == null) return ItemStack.EMPTY;
        return blocks.get(i).getHandler().getItem(0);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        List<SlotModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return ItemStack.EMPTY;
        return blocks.get(i).getHandler().removeItem(0,j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        List<SlotModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return ItemStack.EMPTY;
        return blocks.get(i).getHandler().removeItemNoUpdate(0);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        List<SlotModule> blocks = getBlockEntities();
        if (i > blocks.size() - 1) return;
        if (blocks.get(i) == null) return;
        blocks.get(i).getHandler().setItem(0,itemStack);
    }

    @Override
    public void setChanged() {
        getBlockEntities().stream().filter(Objects::nonNull).forEach(be->be.getHandler().setChanged());
    }

    @Override
    public boolean stillValid(Player player) {
        return getBlockEntities().stream().allMatch(be->be.getHandler().stillValid(player));
    }

    @Override
    public void clearContent() {
        getBlockEntities().forEach(be->be.getHandler().clearContent());
    }


    private List<SlotModule> getBlockEntities(){

        List<SlotModule> blocks = new ArrayList<>();
        BlockPos.betweenClosed(minPos,maxPos).forEach(bp->{
            if (level.getBlockEntity(bp) instanceof SlotModule be){
                blocks.add(be);
            }
        });
        return blocks;
    }

    public List<ItemStack> getItems(){
        return getBlockEntities().stream().flatMap(be->be.getHandler().getItems().stream()).toList();
    }

}

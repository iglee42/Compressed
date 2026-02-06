package fr.iglee42.compressedbox.blocks.modules;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class TankModuleBlock extends ModuleBlock<TankModule> {
    public TankModuleBlock(Properties props) {
        super(props,CBlockEntities.TANK);
    }

    protected boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }
    protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide()) return ItemInteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(blockPos) instanceof TankModule tank)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (itemStack.getItem() instanceof BucketItem bucket){
            if (bucket.arch$getFluid().equals(Fluids.EMPTY)){
                if (tank.getHandler().isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                if (tank.getHandler().getFluid().getAmount() >= 1000){
                    FluidStack stack = tank.getHandler().removeFluid(1000);
                    if (!player.hasInfiniteMaterials()) {
                        ItemStack filledBucket = new ItemStack(stack.getFluid().getBucket());
                        itemStack.consume(1, player);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, filledBucket);
                        } else {
                            player.getInventory().add(filledBucket);
                        }
                    }
                    return ItemInteractionResult.CONSUME_PARTIAL;
                }
            } else {
                if (tank.getHandler().isEmpty()){
                    FluidStack newStack = FluidStack.create(bucket.arch$getFluid(),1000);
                    tank.getHandler().setFluid(newStack);
                    if (!player.hasInfiniteMaterials()) {

                        itemStack.consume(1, player);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, Items.BUCKET.getDefaultInstance());
                        } else {
                            player.getInventory().add(Items.BUCKET.getDefaultInstance());
                        }
                    }
                    return ItemInteractionResult.CONSUME_PARTIAL;
                }
                if (!tank.getHandler().getFluid().isFluidEqual(FluidStack.create(bucket.arch$getFluid(),1))) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                FluidStack inHandler = tank.getHandler().getFluid();
                if (inHandler.getAmount() <= SimpleFluidContainer.MAX_FLUID - 1000){
                    FluidStack newStack = inHandler.copyWithAmount(inHandler.getAmount() + 1000);
                    tank.getHandler().setFluid(newStack);
                    if (!player.hasInfiniteMaterials()) {
                        itemStack.consume(1, player);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, Items.BUCKET.getDefaultInstance());
                        } else {
                            player.getInventory().add(Items.BUCKET.getDefaultInstance());
                        }
                    }
                    return ItemInteractionResult.CONSUME_PARTIAL;
                }
            }
        }
        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }
}

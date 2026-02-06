package fr.iglee42.compressedbox.blocks.modules;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(blockPos) instanceof TankModule tank)) return InteractionResult.PASS;
        if (itemStack.getItem() instanceof BucketItem bucket){
            if (bucket.arch$getFluid().equals(Fluids.EMPTY)){
                if (tank.getHandler().isEmpty()) return InteractionResult.PASS;
                if (tank.getHandler().getFluid().getAmount() >= 1000){
                    FluidStack stack = tank.getHandler().removeFluid(1000);
                    if (!player.isCreative()) {
                        ItemStack filledBucket = new ItemStack(stack.getFluid().getBucket());
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, filledBucket);
                        } else {
                            player.getInventory().add(filledBucket);
                        }
                    }
                    return InteractionResult.CONSUME_PARTIAL;
                }
            } else {
                if (tank.getHandler().isEmpty()){
                    FluidStack newStack = FluidStack.create(bucket.arch$getFluid(),1000);
                    tank.getHandler().setFluid(newStack);
                    if (!player.isCreative()) {

                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, Items.BUCKET.getDefaultInstance());
                        } else {
                            player.getInventory().add(Items.BUCKET.getDefaultInstance());
                        }
                    }
                    return InteractionResult.CONSUME_PARTIAL;
                }
                if (!tank.getHandler().getFluid().isFluidEqual(FluidStack.create(bucket.arch$getFluid(),1))) return InteractionResult.PASS;
                FluidStack inHandler = tank.getHandler().getFluid();
                if (inHandler.getAmount() <= SimpleFluidContainer.MAX_FLUID - 1000){
                    FluidStack newStack = inHandler.copyWithAmount(inHandler.getAmount() + 1000);
                    tank.getHandler().setFluid(newStack);
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            player.setItemInHand(interactionHand, Items.BUCKET.getDefaultInstance());
                        } else {
                            player.getInventory().add(Items.BUCKET.getDefaultInstance());
                        }
                    }
                    return InteractionResult.CONSUME_PARTIAL;
                }
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

}

package fr.iglee42.compressedbox.blocks;

import dev.architectury.platform.Platform;
import fr.iglee42.compressedbox.blockentities.CompressedBlockEntity;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CompressedBlock extends Block implements EntityBlock {

    public static final String BOX_ID_NBT_KEY = "boxID";

    public CompressedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompressedBlockEntity(blockPos,blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.getItemInHand(interactionHand).isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(blockPos) instanceof CompressedBlockEntity be)) return InteractionResult.PASS;
        ServerLevel sLevel = (ServerLevel) level;
        BoxesSaveData manager = BoxesSaveData.get(sLevel);
        Box box;
        if (be.getBoxID() == null) {
            box = manager.createBox((ServerPlayer) player);
            be.setBoxID(box.getId());
        } else {
            UUID boxId = be.getBoxID();
            box = manager.getBox(boxId);
        }

        if (box == null){
            be.setBoxID(null);
            player.displayClientMessage(Component.translatable("message.compressedbox.box_not_found_in_data").withStyle(ChatFormatting.RED),true);
            box = manager.createBox((ServerPlayer) player);
            be.setBoxID(box.getId());
        }
        box.teleportPlayerIn(player,sLevel);
        if (Platform.isDevelopmentEnvironment()) player.displayClientMessage(box.getName().copy().append( Component.literal( ": " + box.getId())),true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        ItemStack stack = super.getCloneItemStack(blockGetter,blockPos,blockState);
        blockGetter.getBlockEntity(blockPos, CBlockEntities.COMPRESSED.get()).ifPresent(be->{
            if (be.getBoxID() != null) stack.getOrCreateTag().putUUID(BOX_ID_NBT_KEY,be.getBoxID());
        });
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> tooltips, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, blockGetter, tooltips, tooltipFlag);
        UUID boxID;
        if ((boxID = getBoxIDFromStack(stack)) != null){
            tooltips.add(Component.literal("Box ID : " + boxID));
        }
    }

    public static @Nullable UUID getBoxIDFromStack(ItemStack stack){
        if (!stack.hasTag()) return null;
        if (stack.getTag() == null) return null;
        if (!stack.getTag().hasUUID(BOX_ID_NBT_KEY)) return null;
        return stack.getTag().getUUID(BOX_ID_NBT_KEY);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return blockEntityType.equals(CBlockEntities.COMPRESSED.get()) ? (lvl,pos,state,be)->CompressedBlockEntity.tick(lvl,pos,state, (CompressedBlockEntity) be) : null;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0f;
    }

}

package fr.iglee42.compressed.blocks;

import dev.architectury.platform.Platform;
import fr.iglee42.compressed.blockentities.CompressedBlockEntity;
import fr.iglee42.compressed.registries.CBlockEntities;
import fr.iglee42.compressed.registries.CDataComponents;
import fr.iglee42.compressed.utils.Box;
import fr.iglee42.compressed.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
    public CompressedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompressedBlockEntity(blockPos,blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(blockPos) instanceof CompressedBlockEntity be)) return InteractionResult.PASS;
        ServerLevel sLevel = (ServerLevel) level;
        BoxesSaveData manager = BoxesSaveData.get(sLevel);
        if (be.getBoxID() == null) {
            Box box = manager.createBox((ServerPlayer) player);
            be.setBoxID(box.getId());
        } else {
            UUID boxId = be.getBoxID();
            Box box = manager.getBox(boxId);
            if (box == null){
                be.setBoxID(null);
                player.displayClientMessage(Component.translatable("message.compressed.box_not_found_in_data").withStyle(ChatFormatting.RED),true);
                box = manager.createBox((ServerPlayer) player);
                be.setBoxID(box.getId());
            }
            box.teleportPlayerIn(player,sLevel);
            if (Platform.isDevelopmentEnvironment()) player.displayClientMessage(box.getName().copy().append( Component.literal( ": " + box.getId())),true);
        }
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        ItemStack stack = super.getCloneItemStack(levelReader,blockPos,blockState);
        levelReader.getBlockEntity(blockPos, CBlockEntities.COMPRESSED.get()).map(CompressedBlockEntity.class::cast).ifPresent(be->{
            if (be.getBoxID() != null) stack.set(CDataComponents.BOX_ID.get(),be.getBoxID());
        });
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltips, TooltipFlag flags) {
        super.appendHoverText(stack, ctx, tooltips, flags);
        if (stack.has(CDataComponents.BOX_ID.get())){
            UUID boxId = stack.get(CDataComponents.BOX_ID.get());
            tooltips.add(Component.literal("Box id : " + boxId));
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return blockEntityType.equals(CBlockEntities.COMPRESSED.get()) ? (lvl,pos,state,be)->CompressedBlockEntity.tick(lvl,pos,state, (CompressedBlockEntity) be) : null;
    }
}

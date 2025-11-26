package fr.iglee42.compressedbox.items;

import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

public class WallPusherItem extends Item {
    public WallPusherItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide) return InteractionResult.sidedSuccess(true);
        BoxesSaveData manager = BoxesSaveData.get(ctx.getLevel());
        Player player = ctx.getPlayer();

        if (player == null) return super.useOn(ctx);
        if (!ctx.getLevel().getBlockState(ctx.getClickedPos()).is(Blocks.BEDROCK)) return super.useOn(ctx);

        Box box = manager.getBoxFromPlayer(player);

        if (box == null){
            player.displayClientMessage(Component.translatable("message.compressedbox.player_box_not_found").withStyle(ChatFormatting.RED),true);
            return super.useOn(ctx);
        }
        Direction face = ctx.getClickedFace();

        if (box.pushWall(face.getOpposite(), (ServerLevel) ctx.getLevel())){
            manager.setDirty();
            if (!player.isCreative()){
                ctx.getItemInHand().shrink(1);
            }
        } else {
            player.displayClientMessage(Component.translatable("message.compressedbox.cannot_push_wall").withStyle(ChatFormatting.RED),true);
        }


        return InteractionResult.CONSUME;
    }
}

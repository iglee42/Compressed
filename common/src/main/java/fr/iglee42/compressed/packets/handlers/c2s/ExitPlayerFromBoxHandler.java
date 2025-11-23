package fr.iglee42.compressed.packets.handlers.c2s;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressed.packets.payloads.c2s.ExitPlayerFromBoxPayload;
import fr.iglee42.compressed.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Set;


public class ExitPlayerFromBoxHandler {

    private static final ExitPlayerFromBoxHandler INSTANCE = new ExitPlayerFromBoxHandler();

    public static ExitPlayerFromBoxHandler instance(){
        return INSTANCE;
    }

    public void handle(final ExitPlayerFromBoxPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            Pair<ResourceKey<Level>, BlockPos> pair = data.getPlayerEntryPoint(player);

            if (pair == null || pair.getFirst() == null || pair.getSecond() == null){
                player.displayClientMessage(Component.translatable("message.compressed.box_not_found_entry_point").withStyle(ChatFormatting.RED),true);
                pair = Pair.of(player.getRespawnDimension(),player.getRespawnPosition());
            }

            if (pair == null || pair.getFirst() == null || pair.getSecond() == null){
                pair = Pair.of(player.server.overworld().dimension(),player.server.overworld().getSharedSpawnPos());
            }

            ServerLevel dimension = player.getServer().getLevel(pair.getFirst());
            BlockPos pos = pair.getSecond();
            if (player.teleportTo(dimension,pos.getX() + 0.5,pos.getY() + 0.5,pos.getZ() + 0.5, Set.of(),player.getXRot(),player.getYRot())){
                data.removePlayerEntryPoint(player);
            }

        });
    }


}

package fr.iglee42.compressedbox.packets.c2s;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.function.Supplier;


public class ExitPlayerFromBoxPacket {

    public static final ExitPlayerFromBoxPacket INSTANCE = new ExitPlayerFromBoxPacket();

    public ExitPlayerFromBoxPacket(FriendlyByteBuf buf) {
    }

    private ExitPlayerFromBoxPacket() {
    }

    public void encode(FriendlyByteBuf buf) {}


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            Pair<ResourceKey<Level>, BlockPos> pair = data.getPlayerEntryPoint(player);

            if (pair == null || pair.getFirst() == null || pair.getSecond() == null){
                player.displayClientMessage(Component.translatable("message.compressedbox.box_not_found_entry_point").withStyle(ChatFormatting.RED),true);
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

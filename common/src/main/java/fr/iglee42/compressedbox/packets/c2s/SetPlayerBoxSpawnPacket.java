package fr.iglee42.compressedbox.packets.c2s;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;


public class SetPlayerBoxSpawnPacket {

    public static final SetPlayerBoxSpawnPacket INSTANCE = new SetPlayerBoxSpawnPacket();

    public SetPlayerBoxSpawnPacket(FriendlyByteBuf buf) {
    }

    private SetPlayerBoxSpawnPacket() {
    }

    public void encode(FriendlyByteBuf buf) {}


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            Box box = data.getBoxFromPlayer(player);
            if (box == null){
                player.displayClientMessage(Component.translatable("message.compressedbox.player_box_not_found").withStyle(ChatFormatting.RED),true);
                return;
            }

            box.setPlayerSpawn(player);
            data.setDirty();
            player.displayClientMessage(Component.translatable("message.compressedbox.box_spawn_modified").withStyle(ChatFormatting.GREEN),true);
        });
    }


}

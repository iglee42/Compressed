package fr.iglee42.compressedbox.packets.handlers.c2s;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.packets.payloads.c2s.SetPlayerBoxSpawnPayload;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class SetPlayerBoxSpawnHandler {

    private static final SetPlayerBoxSpawnHandler INSTANCE = new SetPlayerBoxSpawnHandler();

    public static SetPlayerBoxSpawnHandler instance(){
        return INSTANCE;
    }

    public void handle(final SetPlayerBoxSpawnPayload payload, final NetworkManager.PacketContext context) {
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

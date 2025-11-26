package fr.iglee42.compressedbox.packets.handlers.c2s;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.packets.payloads.c2s.SetBoxNamePayload;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class SetBoxNameHandler {

    private static final SetBoxNameHandler INSTANCE = new SetBoxNameHandler();

    public static SetBoxNameHandler instance(){
        return INSTANCE;
    }

    public void handle(final SetBoxNamePayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            Box box = data.getBox(payload.boxId());
            if (box == null){
                player.displayClientMessage(Component.translatable("message.compressedbox.player_box_not_found").withStyle(ChatFormatting.RED),true);
                return;
            }

            box.setName(payload.name());
            data.setDirty();
            player.displayClientMessage(Component.empty().append(CompressedBox.PREFIX).append(Component.empty().append(Component.translatable("message.compressedbox.box_rename_success",payload.name().replace('&','§'))).withStyle(ChatFormatting.GREEN)),false);
        });
    }


}

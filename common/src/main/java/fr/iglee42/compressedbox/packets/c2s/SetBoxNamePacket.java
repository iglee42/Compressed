package fr.iglee42.compressedbox.packets.c2s;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.function.Supplier;


public record SetBoxNamePacket(UUID boxId, String name) {

    public SetBoxNamePacket(FriendlyByteBuf buf) {
        this(buf.readUUID(),buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(boxId());
        buf.writeUtf(name());
    }


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            Box box = data.getBox(boxId());
            if (box == null){
                player.displayClientMessage(Component.translatable("message.compressedbox.player_box_not_found").withStyle(ChatFormatting.RED),true);
                return;
            }

            box.setName(name());
            data.setDirty();
            player.displayClientMessage(Component.empty().append(CompressedBox.PREFIX).append(Component.empty().append(Component.translatable("message.compressedbox.box_rename_success",name().replace('&','§'))).withStyle(ChatFormatting.GREEN)),false);
        });
    }


}

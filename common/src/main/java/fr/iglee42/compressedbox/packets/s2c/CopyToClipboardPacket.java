package fr.iglee42.compressedbox.packets.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.CompressedClient;
import fr.iglee42.compressedbox.utils.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;


public record CopyToClipboardPacket(String toCopy) {

    public CopyToClipboardPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(toCopy());
    }


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            Minecraft.getInstance().keyboardHandler.setClipboard(toCopy());
        });
    }

}

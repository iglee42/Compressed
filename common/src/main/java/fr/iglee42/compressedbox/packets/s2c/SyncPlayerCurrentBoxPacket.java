package fr.iglee42.compressedbox.packets.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.CompressedClient;
import fr.iglee42.compressedbox.utils.Box;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;


public record SyncPlayerCurrentBoxPacket(Box box) {

    public SyncPlayerCurrentBoxPacket(FriendlyByteBuf buf) {
        this(Box.decode(buf));
    }

    public void encode(FriendlyByteBuf buf) {
        box().encode(buf);
    }


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            CompressedClient.currentBox = box();
        });
    }

}

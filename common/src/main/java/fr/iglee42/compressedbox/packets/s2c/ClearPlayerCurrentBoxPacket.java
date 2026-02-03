package fr.iglee42.compressedbox.packets.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.CompressedClient;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;


public class ClearPlayerCurrentBoxPacket {

    public static final ClearPlayerCurrentBoxPacket INSTANCE = new ClearPlayerCurrentBoxPacket();

    public ClearPlayerCurrentBoxPacket(FriendlyByteBuf buf) {
    }
    private ClearPlayerCurrentBoxPacket() {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            CompressedClient.currentBox = null;
        });
    }


}

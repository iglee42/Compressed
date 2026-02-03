package fr.iglee42.compressedbox.packets.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.gui.ClientConfigScreen;
import fr.iglee42.compressedbox.packets.c2s.SetPlayerBoxSpawnPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;


public class OpenClientConfigScreenPacket {

    public static final OpenClientConfigScreenPacket INSTANCE = new OpenClientConfigScreenPacket();

    public OpenClientConfigScreenPacket(FriendlyByteBuf buf) {
    }

    private OpenClientConfigScreenPacket() {
    }

    public void encode(FriendlyByteBuf buf) {}


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            Minecraft.getInstance().setScreen(new ClientConfigScreen());
        });
    }


}

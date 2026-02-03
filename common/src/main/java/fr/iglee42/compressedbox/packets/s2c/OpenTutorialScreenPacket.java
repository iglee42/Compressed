package fr.iglee42.compressedbox.packets.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.gui.CTutorialScreen;
import fr.iglee42.compressedbox.client.gui.ClientConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;


public class OpenTutorialScreenPacket {

    public static final OpenTutorialScreenPacket INSTANCE = new OpenTutorialScreenPacket();

    public OpenTutorialScreenPacket(FriendlyByteBuf buf) {
    }

    private OpenTutorialScreenPacket() {
    }

    public void encode(FriendlyByteBuf buf) {}


    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        NetworkManager.PacketContext context = ctx.get();
        context.queue(()->{
            Minecraft.getInstance().setScreen(new CTutorialScreen(CTutorialScreen.TutorialPage.INTRO,true));
        });
    }


}

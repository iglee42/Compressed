package fr.iglee42.compressedbox.packets.handlers.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.gui.ClientConfigScreen;
import fr.iglee42.compressedbox.packets.payloads.s2c.OpenClientConfigScreenPayload;
import net.minecraft.client.Minecraft;


public class OpenClientConfigScreenHandler {

    private static final OpenClientConfigScreenHandler INSTANCE = new OpenClientConfigScreenHandler();

    public static OpenClientConfigScreenHandler instance(){
        return INSTANCE;
    }

    public void handle(final OpenClientConfigScreenPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            Minecraft.getInstance().setScreen(new ClientConfigScreen());
        });
    }


}

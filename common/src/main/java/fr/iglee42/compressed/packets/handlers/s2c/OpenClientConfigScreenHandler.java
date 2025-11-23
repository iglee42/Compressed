package fr.iglee42.compressed.packets.handlers.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressed.client.CompressedClient;
import fr.iglee42.compressed.client.gui.ClientConfigScreen;
import fr.iglee42.compressed.packets.payloads.s2c.ClearPlayerCurrentBoxPayload;
import fr.iglee42.compressed.packets.payloads.s2c.OpenClientConfigScreenPayload;
import fr.iglee42.compressed.packets.payloads.s2c.SyncPlayerCurrentBoxPayload;
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

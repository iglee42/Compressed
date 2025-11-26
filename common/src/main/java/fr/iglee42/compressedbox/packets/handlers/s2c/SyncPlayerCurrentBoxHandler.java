package fr.iglee42.compressedbox.packets.handlers.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.CompressedClient;
import fr.iglee42.compressedbox.packets.payloads.s2c.ClearPlayerCurrentBoxPayload;
import fr.iglee42.compressedbox.packets.payloads.s2c.SyncPlayerCurrentBoxPayload;


public class SyncPlayerCurrentBoxHandler {

    private static final SyncPlayerCurrentBoxHandler INSTANCE = new SyncPlayerCurrentBoxHandler();

    public static SyncPlayerCurrentBoxHandler instance(){
        return INSTANCE;
    }

    public void handle(final SyncPlayerCurrentBoxPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            CompressedClient.currentBox = payload.box();
        });
    }

    public void handleClear(final ClearPlayerCurrentBoxPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            CompressedClient.currentBox = null;
        });
    }


}

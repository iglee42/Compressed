package fr.iglee42.compressedbox.packets.handlers.s2c;

import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.client.gui.CTutorialScreen;
import fr.iglee42.compressedbox.packets.payloads.s2c.OpenTutorialScreenPayload;
import net.minecraft.client.Minecraft;


public class OpenTutorialScreenHandler {

    private static final OpenTutorialScreenHandler INSTANCE = new OpenTutorialScreenHandler();

    public static OpenTutorialScreenHandler instance(){
        return INSTANCE;
    }

    public void handle(final OpenTutorialScreenPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            Minecraft.getInstance().setScreen(new CTutorialScreen(CTutorialScreen.TutorialPage.INTRO,true));
        });
    }


}

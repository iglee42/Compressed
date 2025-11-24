package fr.iglee42.compressed.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.client.renderer.modules.ChunkLoadRenderer;
import fr.iglee42.compressed.client.renderer.CompressedBERenderer;
import fr.iglee42.compressed.client.renderer.modules.SlotRenderer;
import fr.iglee42.compressed.packets.handlers.s2c.OpenClientConfigScreenHandler;
import fr.iglee42.compressed.packets.handlers.s2c.SyncPlayerCurrentBoxHandler;
import fr.iglee42.compressed.packets.payloads.c2s.SetBoxNamePayload;
import fr.iglee42.compressed.packets.payloads.s2c.ClearPlayerCurrentBoxPayload;
import fr.iglee42.compressed.packets.payloads.s2c.OpenClientConfigScreenPayload;
import fr.iglee42.compressed.packets.payloads.s2c.SyncPlayerCurrentBoxPayload;
import fr.iglee42.compressed.registries.CBlockEntities;
import fr.iglee42.compressed.utils.Box;
import fr.iglee42.compressed.utils.TutorialRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.lwjgl.glfw.GLFW;

public class CompressedClient {

    public static Box currentBox = null;
    protected static int nameEditCountdown = -1;

    public static final KeyMapping SHOW_BOX_HUD = new KeyMapping("keybind.compressed.show_hud", GLFW.GLFW_KEY_LEFT_ALT,"keybind.compressed.category");

    public static void init(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncPlayerCurrentBoxPayload.TYPE,SyncPlayerCurrentBoxPayload.STREAM_CODEC, SyncPlayerCurrentBoxHandler.instance()::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearPlayerCurrentBoxPayload.TYPE,ClearPlayerCurrentBoxPayload.STREAM_CODEC, SyncPlayerCurrentBoxHandler.instance()::handleClear);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OpenClientConfigScreenPayload.TYPE,OpenClientConfigScreenPayload.STREAM_CODEC, OpenClientConfigScreenHandler.instance()::handle);

        ClientTickEvent.CLIENT_POST.register(e->{
            if (nameEditCountdown > 0) nameEditCountdown--;
            if (nameEditCountdown == 0){
                if (e.player != null) e.player.displayClientMessage(Compressed.PREFIX.copy().append(Component.translatable("message.compressed.box_rename_expired").withStyle(ChatFormatting.RED)),false);
                nameEditCountdown--;
            }
        });

        ClientChatEvent.SEND.register((msg,comp)->{
            if (nameEditCountdown <= -1) return EventResult.pass();
            if (currentBox == null) return EventResult.pass();
            if (msg.trim().equalsIgnoreCase("cancel")) {
                if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.displayClientMessage(Compressed.PREFIX.copy().append(Component.translatable("message.compressed.box_rename_cancelled").withStyle(ChatFormatting.RED)),false);
                nameEditCountdown = -1;
                return EventResult.interruptFalse();
            }
            NetworkManager.sendToServer(new SetBoxNamePayload(currentBox.getId(),msg));
            nameEditCountdown = -1;
            return EventResult.interruptFalse();
        });
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES,new TutorialRegistry());
    }

    public static void registerBER(){
        BlockEntityRendererRegistry.register(CBlockEntities.COMPRESSED.get(), CompressedBERenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.SLOT.get(), SlotRenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.CHUNK_LOAD.get(), ChunkLoadRenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.INFINITE_CHUNK_LOAD.get(), ChunkLoadRenderer::new);
    }



}

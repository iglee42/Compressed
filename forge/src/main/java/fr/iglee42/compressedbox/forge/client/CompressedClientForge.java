package fr.iglee42.compressedbox.forge.client;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.BoxHud;
import fr.iglee42.compressedbox.client.CompressedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CompressedClientForge {

    public static void init(){
        CompressedClient.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(CompressedClientForge::registerKeyMappings);
        modBus.addListener(CompressedClientForge::addRenderLayers);
        modBus.addListener(CompressedClientForge::registerRenderers);
        forgeBus.addListener(CompressedClientForge::onMouseScroll);
        forgeBus.addListener(CompressedClientForge::onUseClicked);
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event){
        event.register(CompressedClient.SHOW_BOX_HUD);
    }

    public static void addRenderLayers(RegisterGuiOverlaysEvent event){
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),  "box_hud", (forgeGui, graphics, f, i, j) -> BoxHud.HUD.accept(graphics));
    }

    public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
        if (!CompressedClient.SHOW_BOX_HUD.isDown()) return;
        int delta = (int) (event.getScrollDelta());
        BoxHud.updateSelectedSlot(delta);
        event.setCanceled(true);
    }

    public static void onUseClicked(InputEvent.InteractionKeyMappingTriggered event){
        if (event.getKeyMapping().equals(Minecraft.getInstance().options.keyUse) && CompressedClient.SHOW_BOX_HUD.isDown()) {
            if (BoxHud.executeSelectedAction()){
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        CompressedClient.registerBER();
    }
}

package fr.iglee42.compressed.fabric.mixins;

import fr.iglee42.compressed.client.BoxHud;
import fr.iglee42.compressed.client.CompressedClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "handleKeybinds",at = @At("HEAD"), cancellable = true)
    private void compressed$overrideUseIfHudOpen(CallbackInfo ci){
        if (Minecraft.getInstance().options.keyUse.isDown() && CompressedClient.SHOW_BOX_HUD.isDown()) {
            if (BoxHud.executeSelectedAction()){
                ci.cancel();
                return;
            }
        }
    }
}

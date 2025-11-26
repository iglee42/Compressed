package fr.iglee42.compressedbox.fabric.mixins;

import fr.iglee42.compressedbox.client.BoxHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "<init>",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;",ordinal = 0,shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void init(Minecraft minecraft, CallbackInfo ci, LayeredDraw layeredDraw, LayeredDraw layeredDraw2){
        layeredDraw2.add((BoxHud.HUD));
    }
}

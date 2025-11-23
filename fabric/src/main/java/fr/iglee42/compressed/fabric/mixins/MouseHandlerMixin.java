package fr.iglee42.compressed.fabric.mixins;

import fr.iglee42.compressed.client.BoxHud;
import fr.iglee42.compressed.client.CompressedClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void updateHudMouse(long l, double d, double e, CallbackInfo ci, boolean bl, double f, double g, double h, int k, int m, int n){
        if (CompressedClient.SHOW_BOX_HUD.isDown()){
            BoxHud.updateSelectedSlot(n);
            ci.cancel();
        }
    }
}

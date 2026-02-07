package fr.iglee42.compressedbox.client.renderer.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.client.EmptyBlockAndTintGetter;
import fr.iglee42.compressedbox.client.LiquidBlockVertexConsumer;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

public class TankRenderer implements BlockEntityRenderer<TankModule> {

    public TankRenderer(BlockEntityRendererProvider.Context context) { }
    @Override
    public void render(TankModule be, float f, PoseStack poseStack, MultiBufferSource buffer, int i, int j) {
        FluidStack stack = be.getHandler().getFluid();
        Minecraft minecraft = Minecraft.getInstance();

        if (!stack.isEmpty()) {
            poseStack.pushPose();
            float fill = (float) stack.getAmount() / SimpleFluidContainer.MAX_FLUID;
            if (fill <= 0) return;
            AABB box = new AABB(
                    1/16D,
                    1/16D,
                    1/16D,
                    15/16D,
                    1/16D + fill * (14/16D),
                    15/16D
            );


            poseStack.translate(box.minX,box.minY,box.minZ);
            poseStack.scale((float) (box.maxX - box.minX), (float) (box.maxY - box.minY) + 5/16f*fill, (float) (box.maxZ - box.minZ));
            Minecraft.getInstance().getBlockRenderer().renderLiquid(
                    be.getBlockPos(),
                    EmptyBlockAndTintGetter.INSTANCE,
                    new LiquidBlockVertexConsumer(buffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(stack.getFluid().defaultFluidState())), poseStack, be.getBlockPos()), be.getBlockState(), stack.getFluid().defaultFluidState()
            );
            poseStack.popPose();
        }
    }

}

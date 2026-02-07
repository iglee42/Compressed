package fr.iglee42.compressedbox.client.renderer.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.client.EmptyBlockAndTintGetter;
import fr.iglee42.compressedbox.client.LiquidBlockVertexConsumer;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import fr.iglee42.compressedbox.utils.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

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

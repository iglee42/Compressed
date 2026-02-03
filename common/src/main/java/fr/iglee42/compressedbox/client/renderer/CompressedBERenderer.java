package fr.iglee42.compressedbox.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.iglee42.compressedbox.blockentities.CompressedBlockEntity;
import fr.iglee42.compressedbox.client.LiquidBlockVertexConsumer;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.mixins.StructureTemplateAccessor;
import fr.iglee42.compressedbox.utils.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class CompressedBERenderer implements BlockEntityRenderer<CompressedBlockEntity> {

    public CompressedBERenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(CompressedBlockEntity be, float pt, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        Box box = be.getBox();
        if (box != null && player != null){
            if (shouldRenderName(minecraft,player,be)){
                if (CClientConfig.get().displayBoxName()) renderNameTag(minecraft,be,box,box.getName(),poseStack,buffer,light);
                if (CClientConfig.get().displayBoxPreview()) renderBox(minecraft,be,box,poseStack,buffer,LightTexture.FULL_BRIGHT,overlay);
            }

        }
    }

    private void renderBox(Minecraft minecraft, CompressedBlockEntity be, Box box, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (minecraft.level == null) return;
        if (be.getBlockInside() == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5,0.2f,0.5);
        double tick = System.currentTimeMillis() / 800.0D;
        poseStack.mulPose(Axis.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
        BlockPos size = new BlockPos(box.getMaxPos().getX() - box.getMinPos().getX(),box.getMaxPos().getY() - box.getMinPos().getY(),box.getMaxPos().getZ() - box.getMinPos().getZ());
        poseStack.scale(1/(32f * (size.getX() / 16f)),1/(32f * (size.getY() / 16f)),1/(32f * (size.getZ() / 16f)));

        StructureTemplate template = be.getBlockInside();
        for (StructureTemplate.Palette palette : ((StructureTemplateAccessor)template).compressed$getPalettes()) {
            for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {
                poseStack.pushPose();
                BlockPos pos = blockInfo.pos().offset(-size.getX() / 2,0,-size.getZ() / 2);
                poseStack.translate(pos.getX(),pos.getY(),pos.getZ());
                renderBlock(blockInfo.state(), pos, poseStack, buffer,light,overlay);

                if (blockInfo.nbt() != null && !blockInfo.nbt().isEmpty()) {
                    BlockEntity te = BlockEntity.loadStatic(pos,blockInfo.state(),blockInfo.nbt());

                    if (!(te instanceof CompressedBlockEntity)) {

                        poseStack.pushPose();
                        try {
                            BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(te);
                            if (renderer != null) {
                                renderer.render(te, 0, poseStack, buffer, light, overlay);
                            }
                        } catch (Exception ignored) {
                        } finally {
                            poseStack.popPose();
                        }
                    }
                }

                poseStack.popPose();

            }
        }


        poseStack.popPose();
    }

    private static void renderBlock(BlockState state, BlockPos pos, PoseStack matrix, MultiBufferSource buffers,int light,int overlay) {
        if (state.liquid()){
            matrix.pushPose();
            matrix.translate(0,2/16f,0);
            if (!state.getFluidState().isEmpty() && Minecraft.getInstance().level != null){
                Minecraft.getInstance().getBlockRenderer().renderLiquid(pos,Minecraft.getInstance().level,new LiquidBlockVertexConsumer(buffers.getBuffer(ItemBlockRenderTypes.getRenderLayer(state.getFluidState())),matrix,pos),state,state.getFluidState());
            }
            matrix.popPose();
            return;
        }
        if (state.getRenderShape() == RenderShape.MODEL) {
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            matrix.pushPose();
            blockRenderer.renderSingleBlock(state, matrix, buffers, light,overlay);
            if (!state.getFluidState().isEmpty() && Minecraft.getInstance().level != null){
                matrix.translate(0,2/16f,0);
                Minecraft.getInstance().getBlockRenderer().renderLiquid(pos,Minecraft.getInstance().level,new LiquidBlockVertexConsumer(buffers.getBuffer(ItemBlockRenderTypes.getRenderLayer(state.getFluidState())),matrix,pos),state,state.getFluidState());
            }
            matrix.popPose();
        }
    }

    protected void renderNameTag(Minecraft minecraft,CompressedBlockEntity be, Box box, Component component, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Font fontRenderer = minecraft.font;
        Quaternionf cameraRotation = minecraft.getEntityRenderDispatcher().cameraOrientation();

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.4F, 0.5F);
        poseStack.mulPose(cameraRotation);
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float backgroundOpacity = minecraft.options.getBackgroundOpacity(0.25F);
        int alpha = (int) (backgroundOpacity * 255.0F) << 24;
        float textOffset = -fontRenderer.width(component) / 2;
        buffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        fontRenderer.drawInBatch(component, textOffset, 0F, 553648127, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, alpha, 0xFFFFFF);
        fontRenderer.drawInBatch(component, textOffset, 0F, -1, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, 0xFFFFFF);

        poseStack.popPose();
    }


    private boolean shouldRenderName(Minecraft minecraft, Player player, CompressedBlockEntity be) {
        if (CClientConfig.get().alwaysDisplayBoxInformation()) return true;
        double sneakRadius = 16;
        if (player.isCrouching()){
            return sneakRadius > 0 && player.position().distanceToSqr(new Vec3(be.getBlockPos().getX() + 0.5, be.getBlockPos().getY() + 0.5,be.getBlockPos().getZ() + 0.5)) <= sneakRadius * sneakRadius;
        }

        return minecraft.hitResult != null && minecraft.hitResult.getType().equals(HitResult.Type.BLOCK) && ((BlockHitResult)minecraft.hitResult).getBlockPos().equals(be.getBlockPos());
    }
}

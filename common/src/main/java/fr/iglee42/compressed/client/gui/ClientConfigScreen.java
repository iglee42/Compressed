package fr.iglee42.compressed.client.gui;

import fr.iglee42.compressed.config.CClientConfig;
import fr.iglee42.compressed.config.CConfigComments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen extends Screen {

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 42);


    private Checkbox boxRenderName;
    private Checkbox boxRenderPreview;
    private Checkbox chunkLoaderBeam;
    private Checkbox chunkLoaderTime;

    public ClientConfigScreen() {
        super(Component.translatable("gui.compressed.client_config"));
    }

    @Override
    protected void init() {
        super.init();
        LinearLayout linearLayout = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        linearLayout.addChild(new StringWidget(getTitle(), this.font), LayoutSettings::alignHorizontallyCenter);
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyLeft();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);



        boxRenderName = rowHelper.addChild(Checkbox
                .builder(Component.literal("Box Display Name"),minecraft.font)
                .selected(CClientConfig.get().displayBoxName())
                .tooltip(Tooltip.create(Component.literal(CConfigComments.boxDisplayName)))
                .build());
        boxRenderPreview = rowHelper.addChild(Checkbox
                .builder(Component.literal("Box Display Preview"),minecraft.font)
                .selected(CClientConfig.get().displayBoxPreview())
                .tooltip(Tooltip.create(Component.literal(CConfigComments.boxDisplayPreview)))
                .build());

        chunkLoaderBeam = rowHelper.addChild(Checkbox
                .builder(Component.literal("Chunk Loader Display Beacon Beam"),minecraft.font)
                .selected(CClientConfig.get().chunkLoaderDisplayBeaconBeam())
                .tooltip(Tooltip.create(Component.literal(CConfigComments.chunkLoaderDisplayBeaconBeam)))
                .build());
        chunkLoaderTime = rowHelper.addChild(Checkbox
                .builder(Component.literal("Chunk Loader Display Remaining Time"),minecraft.font)
                .selected(CClientConfig.get().chunkLoaderDisplayTime())
                .tooltip(Tooltip.create(Component.literal(CConfigComments.chunkLoaderDisplayTime)))
                .build());


        layout.addToContents(gridLayout);
        LinearLayout footer = this.layout.addToFooter(LinearLayout.vertical().spacing(8));
        footer.addChild(new StringWidget(Component.translatable("gui.compressed.client_config.global_warning").withStyle(ChatFormatting.YELLOW), this.font), LayoutSettings::alignHorizontallyCenter);
        footer.addChild(Button.builder(Component.translatable("gui.done"), (btn) -> onClose()).width(200).build());

        layout.visitWidgets(this::addRenderableWidget);

        repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        super.render(guiGraphics, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        CClientConfig.get().setChunkLoaderDisplayBeaconBeam(chunkLoaderBeam.selected());
        CClientConfig.get().setChunkLoaderDisplayTime(chunkLoaderTime.selected());
        CClientConfig.get().setDisplayBoxName(boxRenderName.selected());
        CClientConfig.get().setDisplayBoxPreview(boxRenderPreview.selected());
        CClientConfig.get().save();
        super.onClose();
    }
}

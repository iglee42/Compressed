package fr.iglee42.compressedbox.client.gui;

import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfigComments;
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

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 41, 42);


    private Checkbox boxRenderName;
    private Checkbox boxRenderPreview;
    private Checkbox alwaysDisplayBoxInformation;
    private Checkbox chunkLoaderBeam;
    private Checkbox chunkLoaderTime;

    public ClientConfigScreen() {
        super(Component.translatable("gui.compressedbox.client_config"));
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyLeft();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);



        boxRenderName = rowHelper.addChild(createCheckBox(Component.literal("Box Display Name"),CClientConfig.get().displayBoxName(),Tooltip.create(Component.literal(CConfigComments.boxDisplayName))));
        boxRenderName = rowHelper.addChild(createCheckBox(Component.literal("Box Display Preview"),CClientConfig.get().displayBoxPreview(),Tooltip.create(Component.literal(CConfigComments.boxDisplayPreview))));
        boxRenderName = rowHelper.addChild(createCheckBox(Component.literal("Always Display Box Information"),CClientConfig.get().alwaysDisplayBoxInformation(),Tooltip.create(Component.literal(CConfigComments.alwaysDisplayBoxInformation))));
        boxRenderName = rowHelper.addChild(createCheckBox(Component.literal("Chunk Loader Display Beacon Beam"),CClientConfig.get().chunkLoaderDisplayBeaconBeam(),Tooltip.create(Component.literal(CConfigComments.chunkLoaderDisplayBeaconBeam))));
        boxRenderName = rowHelper.addChild(createCheckBox(Component.literal("Chunk Loader Display Remaining Time"),CClientConfig.get().chunkLoaderDisplayTime(),Tooltip.create(Component.literal(CConfigComments.chunkLoaderDisplayTime))));


        layout.addToContents(gridLayout);
        LinearLayout footer = this.layout.addToFooter(LinearLayout.vertical().spacing(8));
        footer.addChild(new StringWidget(Component.translatable("gui.compressedbox.client_config.global_warning").withStyle(ChatFormatting.YELLOW), this.font), LayoutSettings.defaults().alignHorizontallyCenter());
        footer.addChild(Button.builder(Component.translatable("gui.done"), (btn) -> onClose()).width(200).build());

        layout.visitWidgets(this::addRenderableWidget);

        repositionElements();
    }

    private static Checkbox createCheckBox(Component title, boolean initialValue, Tooltip tooltip){
        Checkbox box = new Checkbox(0,0,20,20,title,initialValue);
        box.setTooltip(tooltip);
        return box;
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(guiGraphics, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        CClientConfig.get().setChunkLoaderDisplayBeaconBeam(chunkLoaderBeam.selected());
        CClientConfig.get().setChunkLoaderDisplayTime(chunkLoaderTime.selected());
        CClientConfig.get().setAlwaysDisplayBoxInformation(alwaysDisplayBoxInformation.selected());
        CClientConfig.get().setDisplayBoxName(boxRenderName.selected());
        CClientConfig.get().setDisplayBoxPreview(boxRenderPreview.selected());
        CClientConfig.get().save();
        super.onClose();
    }
}

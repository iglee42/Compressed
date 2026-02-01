package fr.iglee42.compressedbox.client.gui;

import fr.iglee42.compressedbox.client.gui.components.ItemAndTextButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class CHelpScreen extends Screen {


    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 42);

    public CHelpScreen() {
        super(Component.translatable("gui.compressedbox.tutorial"));
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyLeft();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);

        Arrays.stream(CTutorialScreen.TutorialPage.values()).
                forEach(page->{
                    rowHelper.addChild(ItemAndTextButton.builder(page.getTitle(), btn-> this.minecraft.setScreen(new CTutorialScreen(page,false)),page.getIcon()).width(175).build());
                });

        layout.addToContents(gridLayout);

        layout.visitWidgets(this::addRenderableWidget);

        repositionElements();
    }


    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(guiGraphics, mouseX, mouseY, pt);

    }

}

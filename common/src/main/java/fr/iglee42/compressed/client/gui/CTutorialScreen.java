package fr.iglee42.compressed.client.gui;

import fr.iglee42.compressed.config.CClientConfig;
import fr.iglee42.compressed.config.CConfigComments;
import fr.iglee42.compressed.utils.TutorialRegistry;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CTutorialScreen extends Screen {

    public static final int LINES_PER_PAGE = 14;

    private final TutorialPage page;
    private int totalPages = 0;
    private int pageIndex = 0;
    private boolean canSkip;

    private Button nextButton;
    private Button prevButton;

    public CTutorialScreen(TutorialPage page,boolean canSkip) {
        super(Component.translatable("gui.compressed.tutorial"));
        this.page = page;
        this.canSkip = canSkip;
    }

    @Override
    protected void init() {
        super.init();
        List<FormattedText> lines = this.font.getSplitter().splitLines(TutorialRegistry.getPage(page), this.width - 40, Style.EMPTY);

        totalPages = (int) Math.ceil((double) lines.size() / LINES_PER_PAGE);
        prevButton = this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (pageIndex > 0) {
                pageIndex--;
            }
        }).pos(this.width / 2 - 50, this.height - 40).size(40, 20).build());
        nextButton = this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (pageIndex < totalPages - 1) {
                pageIndex++;
            } else if (pageIndex == totalPages - 1) {
                onClose();
            }
        }).pos(this.width / 2 + 10, this.height - 40).size(40, 20).build());

        if (canSkip) {
            this.addRenderableWidget(Button.builder(Component.literal("Skip Tutorial"), button -> {
                onClose();
            }).pos(this.width / 2 - 75, this.height - 70).size(150, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        super.render(guiGraphics, mouseX, mouseY, pt);
        List<FormattedText> lines = this.font.getSplitter().splitLines(TutorialRegistry.getPage(page), this.width - 40, Style.EMPTY);

        int startLine = pageIndex * LINES_PER_PAGE;
        int endLine = Math.min(startLine + LINES_PER_PAGE, lines.size());
        int y = 40;
        for (int i = startLine; i < endLine; i++) {
            FormattedText line = lines.get(i);
            String raw = line.getString();

            int x = 20;

            if (raw.contains("[item:")) {
                String[] texts = raw.split("\\[item:.+\\]");
                guiGraphics.drawString(this.font, texts[0], x, y, 0xFFFFFF, false);
                x += font.width(texts[0]);

                int start = raw.indexOf("[item:") + "[item:".length();
                int end = raw.indexOf("]", start);
                String itemId = raw.substring(start, end);

                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemId));

                ItemStack stack = new ItemStack(item);
                if (!stack.isEmpty()) {
                    guiGraphics.renderItem(stack, x, y - 4);
                    x += 22;
                }

                Component itemName = item.getName(stack);
                guiGraphics.drawString(this.font, itemName, x, y, 0xFFFFFF, false);
                x += font.width(itemName);
                guiGraphics.drawString(this.font, texts[1], x, y, 0xFFFFFF, false);

                y+=2;

            } else {
                guiGraphics.drawString(this.font, Language.getInstance().getVisualOrder(line), x, y, 0xFFFFFF, false);
            }

            y += this.font.lineHeight + 2;
        }

        String pageIndicator = "Page " + (pageIndex + 1) + " / " + totalPages;
        guiGraphics.drawString(this.font, pageIndicator, this.width / 2 - this.font.width(pageIndicator) / 2, 20, 0xFFFFFF, false);


        if (pageIndex >= totalPages - 1) {
            nextButton.setMessage(Component.literal("Done"));
        } else {
            nextButton.setMessage(Component.literal(">"));
        }
        prevButton.active = pageIndex > 0;

    }

    public static enum TutorialPage {
        INTRO,
        BOX,
        CHUNK_LOADER,
        SLOT
    }
}

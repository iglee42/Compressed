package fr.iglee42.compressedbox.client.gui;

import fr.iglee42.compressedbox.registries.CBlocks;
import fr.iglee42.compressedbox.registries.CItems;
import fr.iglee42.compressedbox.utils.TutorialRegistry;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class CTutorialScreen extends Screen {

    private final TutorialPage page;
    private int totalPages = 0;
    private int linesPerPage = 0;
    private int pageIndex = 0;
    private final boolean canSkip;

    private Button nextButton;
    private Button prevButton;

    public CTutorialScreen(TutorialPage page,boolean canSkip) {
        super(page.getTitle());
        this.page = page;
        this.canSkip = canSkip;
    }

    @Override
    protected void init() {
        super.init();

        pageIndex = 0;
        int topPadding = 60;
        int bottomPadding = 40;
        int availableHeight = this.height - topPadding - bottomPadding;

        linesPerPage = Math.max(1, availableHeight / (this.font.lineHeight + 2));

        List<FormattedText> lines = this.font.getSplitter().splitLines(TutorialRegistry.getPage(page), this.width - 80, Style.EMPTY);

        totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
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
            this.addRenderableWidget(Button.builder(Component.translatable("gui.compressedbox.skip_tutorial"), button -> {
                onClose();
            }).pos(this.width / 2 - 75, this.height - 70).size(150, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        super.render(guiGraphics, mouseX, mouseY, pt);
        List<FormattedText> lines = this.font.getSplitter().splitLines(TutorialRegistry.getPage(page), this.width - 80, Style.EMPTY);

        int startLine = pageIndex * linesPerPage;
        int endLine = Math.min(startLine + linesPerPage, lines.size());
        int y = 60;
        for (int i = startLine; i < endLine; i++) {
            FormattedText line = lines.get(i);
            String raw = line.getString();

            int x = 40;

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

        guiGraphics.drawString(this.font, page.getTitle(), this.width / 2 - this.font.width(page.getTitle()) / 2, 20, 0xFFFFFF, false);
        String pageIndicator = "Page " + (pageIndex + 1) + " / " + totalPages;
        guiGraphics.drawString(this.font, pageIndicator, this.width / 2 - this.font.width(pageIndicator) / 2, 40, 0xFFFFFF, false);


        if (pageIndex >= totalPages - 1) {
            nextButton.setMessage(Component.translatable("gui.done"));
        } else {
            nextButton.setMessage(Component.literal(">"));
        }
        prevButton.active = pageIndex > 0;

    }
    @Getter
    public enum TutorialPage {
        INTRO(CBlocks.COMPRESSED_BLOCK.get()),
        CHUNK_LOADER(CBlocks.INFINITE_CHUNK_LOADER.get()),
        SLOT(CBlocks.SLOT.get()),
        TANK(CBlocks.TANK.get()),
        WALL_PUSHER(CItems.WALL_PUSHER.get())
        ;
        private final ItemStack icon;

        TutorialPage(ItemLike item){
            this.icon = new ItemStack(item.asItem());
        }

        public Component getTitle() {
            return Component.translatable("gui.compressedbox.tutorial." + this.name().toLowerCase());
        }
    }
}

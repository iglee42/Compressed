package fr.iglee42.compressedbox.client.gui.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemAndTextButton extends Button {

    private final ItemStack itemStack;

    protected ItemAndTextButton(int x, int y, int width, int height, Component text, OnPress onPress, CreateNarration createNarration,ItemStack stack) {
        super(x, y, width, height, text, onPress, createNarration);
        this.itemStack = stack;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        super.renderWidget(guiGraphics, mouseX, mouseY, pt);
        guiGraphics.renderItem(itemStack,this.getX() + 2,this.getY() + (this.height - 16) / 2);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int i) {
        this.renderScrollingString(guiGraphics, font, 22, i);
    }

    public static Builder builder(Component component, OnPress onPress,ItemStack stack) {
        return new Builder(component, onPress,stack);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private CreateNarration createNarration;
        private ItemStack stack = ItemStack.EMPTY;

        public Builder(Component arg, OnPress arg2,ItemStack stack) {
            this.createNarration = DEFAULT_NARRATION;
            this.message = arg;
            this.onPress = arg2;
            this.stack = stack;
        }

        public Builder pos(int i, int j) {
            this.x = i;
            this.y = j;
            return this;
        }

        public Builder width(int i) {
            this.width = i;
            return this;
        }

        public Builder size(int i, int j) {
            this.width = i;
            this.height = j;
            return this;
        }

        public Builder bounds(int i, int j, int k, int l) {
            return this.pos(i, j).size(k, l);
        }

        public Builder tooltip(@Nullable Tooltip arg) {
            this.tooltip = arg;
            return this;
        }

        public Builder createNarration(CreateNarration arg) {
            this.createNarration = arg;
            return this;
        }


        public Button build() {
            if (stack.isEmpty()){
                throw new IllegalStateException("ItemStack must be set for ItemAndTextButton");
            }
            return new ItemAndTextButton(x,y,width,height,message,onPress,createNarration,stack);
        }
    }

}

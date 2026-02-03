package fr.iglee42.compressedbox.client;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.gui.CHelpScreen;
import fr.iglee42.compressedbox.client.gui.ClientConfigScreen;
import fr.iglee42.compressedbox.packets.c2s.ExitPlayerFromBoxPacket;
import fr.iglee42.compressedbox.packets.c2s.SetPlayerBoxSpawnPacket;
import fr.iglee42.compressedbox.registries.CNetworking;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoxHud {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    private static int tickPressed = 0;
    private static final int ANIMATION_TIME = 20;
    public static int selectedSlot = 0;

    public static final Consumer<GuiGraphics> HUD = gui -> {
        if (Minecraft.getInstance().options.hideGui) return;
        if (Minecraft.getInstance().level == null || !Minecraft.getInstance().level.dimension().equals(Box.DIMENSION)) return;
        if (CompressedClient.currentBox == null) return;
        int height = 24;
        gui.pose().pushPose();
        if (!CompressedClient.SHOW_BOX_HUD.isDown() && tickPressed <= 0)
        {
            tickPressed = 0;
            gui.pose().popPose();
            return;
        } else if (tickPressed > 0 && !CompressedClient.SHOW_BOX_HUD.isDown()){
            if (tickPressed > ANIMATION_TIME) tickPressed = ANIMATION_TIME;
            float progress = (float) tickPressed / ANIMATION_TIME;
            gui.pose().translate(0,-height * (1-progress),0);
            tickPressed--;
        }

        if (tickPressed <= ANIMATION_TIME && CompressedClient.SHOW_BOX_HUD.isDown()){
            float progress = (float) tickPressed / ANIMATION_TIME;
            gui.pose().translate(0,-height * (1-progress),0);
        }
        gui.blit(WIDGETS_LOCATION, (gui.guiWidth() / 2) -91,0,0,0,182, 22 );
        gui.blit(WIDGETS_LOCATION, (gui.guiWidth() / 2) - 91 - 1 + selectedSlot * 20,  -1,0,22, 24, 23);
        gui.fill((gui.guiWidth() / 2) - 91 - 1 + selectedSlot * 20,  height - 2, (gui.guiWidth() / 2) - 91 - 1 + selectedSlot * 20 + 24, height - 1, 0xFF000000);

        int x = (gui.guiWidth() / 2) - 91;

        for (ResourceLocation sprite : Arrays.stream(MenuAction.values()).map(MenuAction::getSprite).toList()) {
            gui.blit(sprite,x + 3, 3,0,0,16,16,16,16);
            x+=20;
        }

        boolean hasActionSelected = MenuAction.values().length > selectedSlot;
        gui.drawCenteredString(Minecraft.getInstance().font, CompressedClient.currentBox.getName(),gui.guiWidth() / 2,height + (hasActionSelected ? 13 : 2 ),0xffffff);

        if (hasActionSelected){
            gui.drawCenteredString(Minecraft.getInstance().font, MenuAction.values()[selectedSlot].getTitle(),gui.guiWidth() / 2,height + 2,0xffffff);
        }

        gui.pose().popPose();
        if (CompressedClient.SHOW_BOX_HUD.isDown()) tickPressed++;
    };

    public static void updateSelectedSlot(double p_35989_) {
        int i = (int)Math.signum(p_35989_);

        for(selectedSlot -= i; selectedSlot < 0; selectedSlot += 9) {
        }

        while(selectedSlot >= 9) {
            selectedSlot -= 9;
        }

    }

    public static boolean executeSelectedAction() {
        if (MenuAction.getActionBySelected(selectedSlot) == null) return true;
        return MenuAction.getActionBySelected(selectedSlot).execute();
    }

    enum MenuAction{

        SET_SPAWN("set_spawn",()-> CNetworking.CHANNEL.sendToServer(SetPlayerBoxSpawnPacket.INSTANCE)),
        SET_NAME("set_name",()->{
            if (CompressedClient.nameEditCountdown == -1) {
                CompressedClient.nameEditCountdown = 30 * 20;
                Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_rename")), false);
            }
        }),
        EXPORT("export",()-> {
            try {
                StructureTemplate template = CompressedClient.currentBox.createStructureTemplate(Minecraft.getInstance().player.level());
                CompoundTag data = template.save(new CompoundTag());
                String name = CompressedClient.currentBox.getRawName();
                Pattern p = Pattern.compile("&([0-9A-FK-OR])",Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(name);
                Path folder = Services.PLATFORM.getGameDir().resolve("schematics");
                Files.createDirectories(folder);
                String fileName = "export_"+matcher.replaceAll("").toLowerCase(Locale.ROOT).replace(' ','_');
                int counter = 0;
                while (Files.exists(folder.resolve(fileName + (counter > 0 ? "_"+counter : "")+".nbt").toAbsolutePath()) && counter < 50){
                    counter++;
                }
                if (counter == 50){
                    Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_too_many_export").withStyle(ChatFormatting.RED)), false);
                    return;
                }
                if (counter > 0){
                    fileName += "_"+counter;
                }
                fileName += ".nbt";
                if (Files.exists(folder.resolve(fileName+".nbt").toAbsolutePath())){
                    Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_too_many_export").withStyle(ChatFormatting.RED)), false);
                    return;
                }
                try(OutputStream out = Files.newOutputStream(folder.resolve(fileName).toAbsolutePath(), StandardOpenOption.CREATE)){
                    NbtIo.writeCompressed(data,out);
                }
                Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_exported",
                        Component.literal(fileName)
                                .withStyle(Style.EMPTY
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent(
                                                ClickEvent.Action.OPEN_FILE,
                                                folder.toAbsolutePath().toString()
                                        ))))
                        .withStyle(ChatFormatting.GREEN)), false);
            } catch (Exception e){
                Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable( "message.compressedbox.box_export_failed", e.getMessage()).withStyle(ChatFormatting.RED)), false);
            }
        }),
        EXIT_BOX("leave_box",()->CNetworking.CHANNEL.sendToServer(ExitPlayerFromBoxPacket.INSTANCE)),
        CONFIGURE_MOD("configure",()->Minecraft.getInstance().setScreen(new ClientConfigScreen())),
        HELP("help",()->Minecraft.getInstance().setScreen(new CHelpScreen()))
        ;

        private final String icon;
        private final Supplier<Boolean> onClicked;

        MenuAction(String icon, Runnable onClicked) {
            this(icon,()->{
                onClicked.run();
                return true;
            });
        }

        MenuAction(String icon, Supplier<Boolean> onClicked) {
            this.icon = icon;
            this.onClicked = onClicked;
        }

        private ResourceLocation getSprite(){
            return new ResourceLocation(CompressedBox.MODID,"textures/gui/sprites/"+icon+".png");
        }

        private boolean execute(){
            if (Minecraft.getInstance().player == null ) return false;
            if (!Minecraft.getInstance().player.level().dimension().equals(Box.DIMENSION)) return false;
            return onClicked.get();
        }

        private static @Nullable MenuAction getActionBySelected(int selected){
            return Arrays.stream(values()).filter(m->m.ordinal() == selected).findAny().orElse(null);
        }

        private Component getTitle(){
            return Component.translatable("gui.compressedbox.hot_bar."+ name().toLowerCase());
        }
    }
}

package fr.iglee42.compressedbox.utils;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.gui.CTutorialScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class TutorialRegistry extends SimpleJsonResourceReloadListener {

    private static final Map<CTutorialScreen.TutorialPage, Component> PAGES = new HashMap<>();

    public TutorialRegistry() {
        super(new Gson(), "compressed_tutorials");
    }

    public static void loadTutorials(ResourceManager manager) {
        String lang = Minecraft.getInstance().options.languageCode;

        ResourceLocation file = ResourceLocation.fromNamespaceAndPath(CompressedBox.MODID, "compressed_tutorials/" + lang + ".json");

        try {
            Optional<Resource> optional = manager.getResource(file);
            if (optional.isEmpty()) {
                CompressedBox.LOGGER.warn("Tutorial file missing: {}", file);
                return;
            }

            Resource resource = optional.get();
            JsonObject json = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();

            for (CTutorialScreen.TutorialPage page : CTutorialScreen.TutorialPage.values()) {
                String key = page.name().toLowerCase();

                if (json.has(key)) {
                    JsonArray arr = json.getAsJsonArray(key);

                    List<Component> components = new ArrayList<>();
                    for (JsonElement el : arr) {
                        components.add(parseComponent(el));
                    }

                    PAGES.put(page, components.stream().reduce(Component.empty(),(sum,comp)->sum.copy().append(comp)));
                }
            }
        }
        catch (Exception e) {
            CompressedBox.LOGGER.error("Failed to load tutorial file: {}", file, e);
        }
    }

    public static Component getPage(CTutorialScreen.TutorialPage page) {
        return PAGES.getOrDefault(page,
                Component.literal("Missing tutorial page: " + page.name()));
    }

    private static Component parseComponent(JsonElement el) {
        JsonObject obj = el.getAsJsonObject();

        if (obj.has("text") || obj.has("translate") || obj.has("keybind") || obj.has("score") || obj.has("selector")) {
            return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, obj).mapOrElse(c -> c, err -> Component.literal("Failed to load tutorial line."));
        }

        return Component.literal("Invalid tutorial line.");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        loadTutorials(resourceManager);
    }
}

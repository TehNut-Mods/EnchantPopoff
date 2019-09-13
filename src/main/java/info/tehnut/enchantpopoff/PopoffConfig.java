package info.tehnut.enchantpopoff;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PopoffConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @JsonAdapter(FormattingSerializer.class)
    private Formatting defaultColor = Formatting.AQUA;
    @JsonAdapter(ColorOverrideSerializer.class)
    private Map<Identifier, Formatting> colorOverrides = Maps.newHashMap();
    @JsonAdapter(FormattingSerializer.class)
    private Formatting maxLevelFormat = Formatting.ITALIC;
    private boolean mergeLines = true;

    public Formatting getDefaultColor() {
        return defaultColor;
    }

    public Formatting getOverride(Enchantment enchantment) {
        return colorOverrides.getOrDefault(Registry.ENCHANTMENT.getId(enchantment), defaultColor);
    }

    public Formatting getMaxLevelFormat() {
        return maxLevelFormat;
    }

    public boolean shouldMergeLines() {
        return mergeLines;
    }

    public static PopoffConfig load() {
        File file = new File(FabricLoader.getInstance().getConfigDirectory(), "enchant_popoff.json");
        if (!file.exists()) {
            PopoffConfig config = new PopoffConfig();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(GSON.toJson(config));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        } else {
            try (FileReader reader = new FileReader(file)) {
                return new Gson().fromJson(reader, PopoffConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new PopoffConfig();
    }

    public static class ColorOverrideSerializer implements JsonSerializer<Map<Identifier, Formatting>>, JsonDeserializer<Map<Identifier, Formatting>> {
        @Override
        public Map<Identifier, Formatting> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, String> deserialized = context.deserialize(json, new TypeToken<Map<String, String>>(){}.getType());
            return deserialized.entrySet().stream().collect(Collectors.toMap(o -> new Identifier(o.getKey()), o -> getFormatting(o.getValue())));
        }

        @Override
        public JsonElement serialize(Map<Identifier, Formatting> src, Type typeOfSrc, JsonSerializationContext context) {
            Map<String, String> ret = src.entrySet().stream().collect(Collectors.toMap(o -> o.getKey().toString(), o -> o.getValue().toString()));
            return context.serialize(ret, new TypeToken<Map<String, String>>(){}.getType());
        }
    }

    public static class FormattingSerializer implements JsonDeserializer<Formatting> {
        @Override
        public Formatting deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return getFormatting(json.getAsString());
        }
    }

    private static Formatting getFormatting(String name) {
        Formatting formatting = Formatting.AQUA;
        try {
            formatting = Formatting.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e ) {
            // No-op
        }
        return formatting;
    }
}

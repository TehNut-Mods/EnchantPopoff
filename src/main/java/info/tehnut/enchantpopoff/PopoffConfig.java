package info.tehnut.enchantpopoff;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormat;
import net.minecraft.enchantment.Enchantment;
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

    @JsonAdapter(ChatFormatSerializer.class)
    private ChatFormat defaultColor = ChatFormat.AQUA;
    @JsonAdapter(ColorOverrideSerializer.class)
    private Map<Identifier, ChatFormat> colorOverrides = Maps.newHashMap();
    @JsonAdapter(ChatFormatSerializer.class)
    private ChatFormat maxLevelFormat = ChatFormat.ITALIC;
    private boolean mergeLines = true;

    public ChatFormat getDefaultColor() {
        return defaultColor;
    }

    public ChatFormat getOverride(Enchantment enchantment) {
        return colorOverrides.getOrDefault(Registry.ENCHANTMENT.getId(enchantment), defaultColor);
    }

    public ChatFormat getMaxLevelFormat() {
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

    public static class ColorOverrideSerializer implements JsonSerializer<Map<Identifier, ChatFormat>>, JsonDeserializer<Map<Identifier, ChatFormat>> {
        @Override
        public Map<Identifier, ChatFormat> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, String> deserialized = context.deserialize(json, new TypeToken<Map<String, String>>(){}.getType());
            return deserialized.entrySet().stream().collect(Collectors.toMap(o -> new Identifier(o.getKey()), o -> getChatFormat(o.getValue())));
        }

        @Override
        public JsonElement serialize(Map<Identifier, ChatFormat> src, Type typeOfSrc, JsonSerializationContext context) {
            Map<String, String> ret = src.entrySet().stream().collect(Collectors.toMap(o -> o.getKey().toString(), o -> o.getValue().toString()));
            return context.serialize(ret, new TypeToken<Map<String, String>>(){}.getType());
        }
    }

    public static class ChatFormatSerializer implements JsonDeserializer<ChatFormat> {
        @Override
        public ChatFormat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return getChatFormat(json.getAsString());
        }
    }

    private static ChatFormat getChatFormat(String name) {
        ChatFormat chatFormat = ChatFormat.AQUA;
        try {
            chatFormat = ChatFormat.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e ) {
            // No-op
        }
        return chatFormat;
    }
}

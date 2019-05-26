package info.tehnut.enchantpopoff;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class PopoffHelper {

    public static final PopoffConfig CONFIG = PopoffConfig.load();

    public static boolean handlePopoff(ItemStack stack, MinecraftClient client, String name, int xPos, int yPos, int alpha, int scaledWidth) {
        if (!stack.hasEnchantments())
            return false;

        List<Component> enchantmentTooltips = PopoffHelper.getEnchantmentTooltip(stack);
        if (CONFIG.shouldMergeLines())
            enchantmentTooltips = PopoffHelper.combineLines(enchantmentTooltips, client.textRenderer, scaledWidth);
        yPos -= (enchantmentTooltips.size() * client.textRenderer.fontHeight) - 2;
        client.textRenderer.drawWithShadow(name, xPos, yPos, 16777215 + (alpha << 24));
        yPos += 2;
        for (Component line : enchantmentTooltips) {
            yPos += client.textRenderer.fontHeight + 1;
            String lineText = line.getFormattedText();
            int drawX = (scaledWidth - client.textRenderer.getStringWidth(lineText)) / 2;
            client.textRenderer.drawWithShadow(lineText, drawX, yPos, 16777215 + (alpha << 24));
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        return true;
    }

    private static List<Component> getEnchantmentTooltip(ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        EnchantmentHelper.getEnchantments(stack).forEach((enchantment, level) -> {
            Component line = enchantment.getTextComponent(level);
            line.modifyStyle(style -> {
                style.setColor(CONFIG.getOverride(enchantment));
                if (enchantment.getMaximumLevel() <= level) {
                    ChatFormat maxLevel = CONFIG.getMaxLevelFormat();
                    if (maxLevel.isColor())
                        style.setColor(maxLevel);
                    else {
                        switch (maxLevel) {
                            case BOLD: style.setBold(true); break;
                            case ITALIC: style.setItalic(true); break;
                            case OBFUSCATED: style.setObfuscated(true); break;
                            case STRIKETHROUGH: style.setStrikethrough(true); break;
                            case UNDERLINE: style.setUnderline(true); break;
                            case RESET: break;
                        }
                    }
                }
            });
            components.add(line);
        });
        return components;
    }

    private static List<Component> combineLines(List<Component> components, TextRenderer textRenderer, int maxWidth) {
        List<Component> merged = Lists.newArrayList();
        Component current = new TextComponent("");
        for (Component entry : components) {
            if (textRenderer.getStringWidth(current.getFormattedText()) > maxWidth / 2) {
                merged.add(current);
                current = new TextComponent("");
            }

            current.append(current.getSiblings().isEmpty() ? "" : " | ").append(entry);
        }

        merged.add(current);
        return merged;
    }
}

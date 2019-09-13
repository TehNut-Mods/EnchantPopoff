package info.tehnut.enchantpopoff;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PopoffHelper {

    public static final PopoffConfig CONFIG = PopoffConfig.load();

    public static boolean handlePopoff(ItemStack stack, MinecraftClient client, String name, int xPos, int yPos, int alpha, int scaledWidth) {
        if (!stack.hasEnchantments())
            return false;

        List<Text> enchantmentTooltips = PopoffHelper.getEnchantmentTooltip(stack);
        if (CONFIG.shouldMergeLines())
            enchantmentTooltips = PopoffHelper.combineLines(enchantmentTooltips, client.textRenderer, scaledWidth);
        yPos -= (enchantmentTooltips.size() * client.textRenderer.fontHeight) - 2;
        client.textRenderer.drawWithShadow(name, xPos, yPos, 16777215 + (alpha << 24));
        yPos += 2;
        for (Text line : enchantmentTooltips) {
            yPos += client.textRenderer.fontHeight + 1;
            String lineText = line.asFormattedString();
            int drawX = (scaledWidth - client.textRenderer.getStringWidth(lineText)) / 2;
            client.textRenderer.drawWithShadow(lineText, drawX, yPos, 16777215 + (alpha << 24));
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        return true;
    }

    private static List<Text> getEnchantmentTooltip(ItemStack stack) {
        List<Text> Texts = Lists.newArrayList();
        EnchantmentHelper.getEnchantments(stack).forEach((enchantment, level) -> {
            Text line = enchantment.getName(level);
            line.styled(style -> {
                style.setColor(CONFIG.getOverride(enchantment));
                if (enchantment.getMaximumLevel() <= level) {
                    Formatting maxLevel = CONFIG.getMaxLevelFormat();
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
            Texts.add(line);
        });
        return Texts;
    }

    private static List<Text> combineLines(List<Text> Texts, TextRenderer textRenderer, int maxWidth) {
        List<Text> merged = Lists.newArrayList();
        Text current = new LiteralText("");
        for (Text entry : Texts) {
            if (textRenderer.getStringWidth(current.asFormattedString()) > maxWidth / 2) {
                merged.add(current);
                current = new LiteralText("");
            }

            current.append(current.getSiblings().isEmpty() ? "" : " | ").append(entry);
        }

        merged.add(current);
        return merged;
    }
}

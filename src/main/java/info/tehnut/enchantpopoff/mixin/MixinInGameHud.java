package info.tehnut.enchantpopoff.mixin;

import info.tehnut.enchantpopoff.PopoffHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    private ItemStack currentStack;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledWidth;

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void enchantpopoff$renderEnchantmentPopoff(CallbackInfo callbackInfo, Text component, String name, int xPos, int yPos, int alpha) {
        if (PopoffHelper.handlePopoff(currentStack, client, name, xPos, yPos, alpha, scaledWidth))
            callbackInfo.cancel();
    }
}

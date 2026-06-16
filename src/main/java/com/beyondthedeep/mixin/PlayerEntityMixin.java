package com.beyondthedeep.mixin;

import com.beyondthedeep.items.ModArmorMaterials;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    // 1. Отмена урона от падения (метод возвращает boolean, поэтому используем Returnable)
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void voidArmorFallProtection(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (ModArmorMaterials.hasFullVoidArmor(player)) {
            cir.setReturnValue(false); // Говорим игре: "урона не было"
        }
    }

    // 2. Ночное зрение (метод ничего не возвращает, используем обычный CallbackInfo)
    @Inject(method = "tick", at = @At("TAIL"))
    private void applyVoidArmorEffects(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (ModArmorMaterials.hasFullVoidArmor(player)) {
            if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 220, 0, false, false, true
                ));
            }
        }
    }
}
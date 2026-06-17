package com.beyondthedeep.mixin;

import com.beyondthedeep.items.custom.VoidRequiemItem;
import com.beyondthedeep.event.ModEvents; // Импортируем наш класс событий
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class VoidRequiemMixin {

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void removeVoidRequiemBeforeDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        PlayerInventory inv = player.getInventory();

        // Создаем список для всех мечей игрока
        List<ItemStack> savedSwords = new java.util.ArrayList<>();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.getItem() instanceof VoidRequiemItem) {
                savedSwords.add(stack.copy());
                inv.removeStack(i);
            }
        }

        // Если нашли хоть один меч, сохраняем весь список
        if (!savedSwords.isEmpty()) {
            ModEvents.saveSwordsForPlayer(player.getUuid(), savedSwords);
        }
    }
}
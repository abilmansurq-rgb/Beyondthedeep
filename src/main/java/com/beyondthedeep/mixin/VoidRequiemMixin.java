package com.beyondthedeep.mixin;

import com.beyondthedeep.event.ModEvents;
import com.beyondthedeep.items.SoulboundItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public class VoidRequiemMixin {

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void removeSoulboundItemsBeforeDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        PlayerInventory inv = player.getInventory();

        // Создаем список для всех предметов Soulbound
        List<ItemStack> savedItems = new ArrayList<>();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            // Если предмет помечан как Soulbound, забираем его из инвентаря
            if (stack.getItem() instanceof com.beyondthedeep.items.SoulboundItem) {
                savedItems.add(stack.copy());
                inv.removeStack(i);
            }
        }

        // Если нашли хоть один предмет, сохраняем весь список в хранилище ModEvents
        if (!savedItems.isEmpty()) {
            ModEvents.saveSwordsForPlayer(player.getUuid(), savedItems);
        }
    }
}
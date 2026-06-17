package com.beyondthedeep.event;

import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
// ... импорты

public class ModEvents {
    public static void registerEvents() {
        // Здесь будут все события нашего мода
        registerAttackEvents();
        registerDeathEvents();

        System.out.println("[BeyondTheDeep] ModEvents initialized and ready.");
    }

    private static void registerAttackEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof net.minecraft.entity.player.PlayerEntity player) {
                ItemStack stack = player.getMainHandStack();

                // Проверяем, что в руке наш меч
                if (stack.getItem() instanceof VoidRequiemItem) {
                    float gain = (killedEntity instanceof net.minecraft.entity.mob.Monster) ? 0.010f : 0.005f;

                    // Обновляем NBT
                    NbtCompound nbt = stack.getOrCreateNbt();
                    float currentBonus = nbt.getFloat("DamageBonus");
                    nbt.putFloat("DamageBonus", currentBonus + gain);

                    // Если хочешь добавить визуальный или звуковой отклик:
                    // player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                }
            }
        });
    }

    private static void registerDeathEvents() {
        // Сюда добавим логику штрафа 25% при смерти
    }
}
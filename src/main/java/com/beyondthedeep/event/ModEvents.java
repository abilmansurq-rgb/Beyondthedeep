package com.beyondthedeep.event;

import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    public static void registerDeathEvents() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // Проходим по инвентарю
            for (int i = 0; i < newPlayer.getInventory().size(); i++) {
                var stack = newPlayer.getInventory().getStack(i);

                if (stack.getItem() instanceof VoidRequiemItem) {
                    var nbt = stack.getOrCreateNbt();
                    float bonus = nbt.getFloat("DamageBonus");

                    if (bonus > 0.05f) {
                        // Уменьшаем на 20% (оставляем 80%)
                        nbt.putFloat("DamageBonus", bonus * 0.8f);
                        // 1. Добавляем звук «потухания» (звук разбитого стекла или магии)
                        newPlayer.getWorld().playSound(null, newPlayer.getX(), newPlayer.getY(), newPlayer.getZ(),
                                SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0f, 0.5f);

                        ServerWorld serverWorld = (ServerWorld) newPlayer.getWorld();

                        serverWorld.spawnParticles(
                                ParticleTypes.SCULK_SOUL, // Тип частицы
                                newPlayer.getX(), newPlayer.getY() + 1.0, newPlayer.getZ(), // Позиция
                                20, // Количество
                                0.5, 0.5, 0.5, // Разброс (delta)
                                0.1 // Скорость
                        );;
                        newPlayer.sendMessage(Text.translatable("message.beyondthedeep.void_echo").formatted(Formatting.RED), true);
                    } else {
                        nbt.putFloat("DamageBonus", 0.0f);
                    }
                }
            }
        });
    }
}
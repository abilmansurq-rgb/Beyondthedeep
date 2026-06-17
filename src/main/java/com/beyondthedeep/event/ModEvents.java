package com.beyondthedeep.event;

import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModEvents {

    public static void registerEvents() {
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

                    // Обновляем NBT бонус урона
                    NbtCompound nbt = stack.getOrCreateNbt();
                    float currentBonus = nbt.getFloat("DamageBonus");
                    nbt.putFloat("DamageBonus", currentBonus + gain);
                }
            }
        });
    }

    public static void registerDeathEvents() {
        // COPY_FROM срабатывает при смерти, позволяя перенести вещи со старого игрока на нового
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            System.out.println("DEBUG: Попытка переноса меча при возрождении!");
            // Ищем меч в инвентаре УМЕРШЕГО игрока
            for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
                ItemStack stack = oldPlayer.getInventory().getStack(i);

                if (stack.getItem() instanceof VoidRequiemItem) {
                    NbtCompound nbt = stack.getOrCreateNbt();
                    float bonus = nbt.getFloat("DamageBonus");

                    // Применяем штраф 20% при каждой смерти
                    if (bonus > 0.05f) {
                        float newBonus = bonus * 0.8f;
                        nbt.putFloat("DamageBonus", newBonus);

                        // Звуковой эффект "потухания" силы
                        newPlayer.getWorld().playSound(null, newPlayer.getX(), newPlayer.getY(), newPlayer.getZ(),
                                SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0f, 0.5f);

                        // Визуальный эффект частицы души
                        if (newPlayer.getWorld() instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                                    newPlayer.getX(), newPlayer.getY() + 1.0, newPlayer.getZ(),
                                    20, 0.5, 0.5, 0.5, 0.1);
                        }

                        // Уведомление в Action Bar
                        newPlayer.sendMessage(Text.translatable("message.beyondthedeep.void_echo").formatted(Formatting.RED), true);
                    } else {
                        nbt.putFloat("DamageBonus", 0.0f);
                    }

                    // "Телепортация" меча новому игроку (Soulbound)
                    newPlayer.getInventory().insertStack(stack);
                }
            }
        });
    }
}
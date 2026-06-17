package com.beyondthedeep.event;

import com.beyondthedeep.items.SoulboundItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModEvents {
    // Статическое хранилище для предметов Soulbound на случай смерти
    private static final Map<UUID, List<ItemStack>> deathStorage = new HashMap<>();

    public static void saveSwordsForPlayer(UUID uuid, List<ItemStack> stacks) {
        deathStorage.put(uuid, stacks);
    }

    public static void registerEvents() {
        registerAttackEvents();
        registerDeathEvents();
        System.out.println("[BeyondTheDeep] ModEvents initialized and ready.");
    }

    private static void registerAttackEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof net.minecraft.entity.player.PlayerEntity player) {
                ItemStack stack = player.getMainHandStack();
                // Логика прокачки остается специфичной для меча
                if (stack.getItem() instanceof VoidRequiemItem) {
                    float gain = (killedEntity instanceof net.minecraft.entity.mob.Monster) ? 0.010f : 0.005f;
                    NbtCompound nbt = stack.getOrCreateNbt();
                    float currentBonus = nbt.getFloat("DamageBonus");
                    nbt.putFloat("DamageBonus", currentBonus + gain);
                }
            }
        });
    }

    public static void registerDeathEvents() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // Забираем список спасенных предметов из хранилища
            List<ItemStack> savedStacks = deathStorage.remove(newPlayer.getUuid());

            if (savedStacks != null && !savedStacks.isEmpty()) {
                for (ItemStack stack : savedStacks) {
                    // Применяем логику штрафа к бонусу, если предмет — меч
                    if (stack.getItem() instanceof VoidRequiemItem) {
                        NbtCompound nbt = stack.getOrCreateNbt();
                        float bonus = nbt.getFloat("DamageBonus");

                        if (bonus > 0.05f) {
                            nbt.putFloat("DamageBonus", bonus * 0.8f);
                        } else {
                            nbt.putFloat("DamageBonus", 0.0f);
                        }
                    }
                    // Возвращаем предмет в инвентарь нового игрока
                    newPlayer.getInventory().insertStack(stack);
                }

                // Звуковые и визуальные эффекты после возрождения
                newPlayer.getWorld().playSound(null, newPlayer.getX(), newPlayer.getY(), newPlayer.getZ(),
                        SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0f, 0.5f);

                if (newPlayer.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                            newPlayer.getX(), newPlayer.getY() + 1.0, newPlayer.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);
                }
                newPlayer.sendMessage(Text.translatable("message.beyondthedeep.void_echo").formatted(Formatting.RED), true);
            }
        });
    }
}
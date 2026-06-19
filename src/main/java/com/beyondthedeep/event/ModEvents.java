package com.beyondthedeep.event;

import com.beyondthedeep.enchantments.ModEnchantments;
import com.beyondthedeep.items.ModItems;
import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.*;

public class ModEvents {
    // Хранилище предметов игрока на случай смерти
    private static final Map<UUID, List<ItemStack>> deathStorage = new HashMap<>();

    public static void saveSwordsForPlayer(UUID uuid, List<ItemStack> stacks) {
        deathStorage.put(uuid, stacks);
    }

    public static void registerEvents() {
        registerAttackEvents();
        registerDeathEvents();
        registerRitualEvents();
        registerEnchantmentEffects();
        System.out.println("[BeyondTheDeep] ModEvents fully initialized with Evolution Rituals.");
    }

    /**
     * Основной цикл ритуалов: Крафт и Эволюция меча
     */
    private static void registerRitualEvents() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // Оптимизация: проверять ритуал каждые 2 секунды (40 тиков)
            if (world.getTime() % 40 != 0) return;

            for (PlayerEntity player : world.getPlayers()) {
                // Ищем предметы вокруг игрока в радиусе 5 блоков
                Box searchArea = player.getBoundingBox().expand(5.0);
                List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, searchArea, e -> true);

                for (ItemEntity item : items) {
                    // Меч должен лежать на земле хотя бы 3 секунды, чтобы ритуал активировался
                    if (!item.isOnGround() || item.getItemAge() < 60) continue;

                    ItemStack stack = item.getStack();

                    // --- СЦЕНАРИЙ 1: КРАФТ (Alloy Sword -> Requiem I) ---
                    if (stack.getItem() == ModItems.VOID_ALLOY_SWORD && !stack.isDamaged()) {
                        List<ItemEntity> cores = getNearby(item, ModItems.VOID_CORE, 2.0);
                        List<ItemEntity> shards = getNearby(item, ModItems.VOID_SHARD, 2.0);

                        // Для крафта нужно 1 ядро и 8 осколков
                        if (!cores.isEmpty() && countShards(shards) >= 8) {
                            performRitual(world, item, 8, false);
                        }
                    }

                    // --- СЦЕНАРИЙ 2: ЭВОЛЮЦИЯ (Requiem I -> II -> III) ---
                    else if (stack.getItem() == ModItems.VOID_REQUIEM) {
                        NbtCompound nbt = stack.getOrCreateNbt();
                        int ritualCount = nbt.getInt("RitualCount");

                        // Максимум 2 эволюции (стадии 0, 1, 2)
                        if (ritualCount < 2) {
                            List<ItemEntity> cores = getNearby(item, ModItems.VOID_CORE, 2.0);
                            List<ItemEntity> shards = getNearby(item, ModItems.VOID_SHARD, 2.0);

                            // Для эволюции нужно 1 ядро и 16 осколков
                            if (!cores.isEmpty() && countShards(shards) >= 16) {
                                performRitual(world, item, 16, true);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Поиск предметов определенного типа рядом с центром
     */
    private static List<ItemEntity> getNearby(ItemEntity center, Item item, double radius) {
        return center.getWorld().getEntitiesByClass(ItemEntity.class, center.getBoundingBox().expand(radius), e -> e.getStack().getItem() == item);
    }

    /**
     * Суммирует общее количество предметов в стаках
     */
    private static int countShards(List<ItemEntity> entities) {
        return entities.stream().mapToInt(e -> e.getStack().getCount()).sum();
    }

    /**
     * Основная логика: визуальные эффекты + создание нового/улучшенного меча
     */
    private static void performRitual(World world, ItemEntity center, int cost, boolean isUpgrade) {
        ServerWorld sw = (ServerWorld) world;

        // Эффекты: кольцо частиц Sculk Soul
        for (int i = 0; i < 360; i += 15) {
            double x = Math.cos(i * Math.PI / 180) * 1.5;
            double z = Math.sin(i * Math.PI / 180) * 1.5;
            sw.spawnParticles(ParticleTypes.SCULK_SOUL, center.getX() + x, center.getY() + 0.5, center.getZ() + z, 2, 0.1, 0.1, 0.1, 0.05);
        }
        world.playSound(null, center.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS, 2.0f, 0.5f);
        world.playSound(null, center.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.BLOCKS, 1.0f, 1.0f);

        ItemStack resultStack;
        if (!isUpgrade) {
            // --- ЛОГИКА КРАФТА ---
            resultStack = new ItemStack(ModItems.VOID_REQUIEM);
            // Берем чары с Alloy меча
            Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.get(center.getStack()));
            // Апгрейдим всё на +1
            upgradeEnchantmentLevels(enchants);
            // Добавляем наши эксклюзивные чары, если их еще нет
            enchants.putIfAbsent(ModEnchantments.ECHO_ACCELERATION, 1);
            enchants.putIfAbsent(ModEnchantments.VOID_LIMIT, 1);
            EnchantmentHelper.set(enchants, resultStack);
        } else {
            // --- ЛОГИКА ЭВОЛЮЦИИ ---
            resultStack = center.getStack().copy();
            NbtCompound nbt = resultStack.getOrCreateNbt();
            nbt.putInt("RitualCount", nbt.getInt("RitualCount") + 1);

            // Повышаем уровень всех существующих зачарований на +1
            Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.get(resultStack));
            upgradeEnchantmentLevels(enchants);
            EnchantmentHelper.set(enchants, resultStack);
        }

        // Спавним финальный меч
        world.spawnEntity(new ItemEntity(world, center.getX(), center.getY() + 0.5, center.getZ(), resultStack));

        // Удаление ингредиентов
        center.discard();
        getNearby(center, ModItems.VOID_CORE, 2.0).get(0).discard();

        int removed = 0;
        for (ItemEntity shardEntity : getNearby(center, ModItems.VOID_SHARD, 2.0)) {
            int count = shardEntity.getStack().getCount();
            if (removed + count <= cost) { shardEntity.discard(); removed += count; }
            else { shardEntity.getStack().decrement(cost - removed); break; }
        }
    }

    /**
     * Повышает уровень каждого зачарования в карте на 1
     */
    private static void upgradeEnchantmentLevels(Map<Enchantment, Integer> enchants) {
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            // Исключаем чары, которые не могут иметь уровней (типа Mending)
            if (enchantment == net.minecraft.enchantment.Enchantments.MENDING ||
                    enchantment == net.minecraft.enchantment.Enchantments.SILK_TOUCH) continue;

            int currentLevel = entry.getValue();
            enchants.put(enchantment, currentLevel + 1);
        }
    }

    private static void registerAttackEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof PlayerEntity player) {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof VoidRequiemItem voidItem) {
                    int level = EnchantmentHelper.getLevel(ModEnchantments.ECHO_ACCELERATION, stack);
                    float multiplier = (level > 0) ? (float) Math.pow(2, level) : 1.0f;
                    float baseGain = (killedEntity instanceof net.minecraft.entity.mob.Monster) ? 0.010f : 0.005f;

                    NbtCompound nbt = stack.getOrCreateNbt();
                    float currentBonus = nbt.getFloat("DamageBonus");
                    nbt.putFloat("DamageBonus", Math.min(currentBonus + (baseGain * multiplier), (float) voidItem.getVoidLimit(stack)));
                }
            }
        });
    }

    private static void registerEnchantmentEffects() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (!(entity instanceof PlayerEntity player)) return;

            ItemStack stack = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(ModEnchantments.ABSORB, stack);

            if (level > 0) {
                // Уровень 1: Пылесос лута
                Box box = player.getBoundingBox().expand(8.0);
                List<net.minecraft.entity.ItemEntity> items = world.getEntitiesByClass(net.minecraft.entity.ItemEntity.class, box, e -> true);
                for (net.minecraft.entity.ItemEntity item : items) {
                    item.setPosition(player.getX(), player.getY(), player.getZ());
                }

                // 2. Собираем сферы опыта
                List<net.minecraft.entity.ExperienceOrbEntity> orbs = world.getEntitiesByClass(net.minecraft.entity.ExperienceOrbEntity.class, box, e -> true);
                for (net.minecraft.entity.ExperienceOrbEntity orb : orbs) {
                    orb.setPosition(player.getX(), player.getY(), player.getZ());
                }

                // Уровень 2: Хил
                if (level >= 2) player.heal(1.5f);

                // Уровень 3: Сила
                if (level >= 3) player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.STRENGTH, 200, 0));
            }
        });
    }

    public static void registerDeathEvents() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            List<ItemStack> savedStacks = deathStorage.remove(newPlayer.getUuid());
            if (savedStacks != null) {
                for (ItemStack stack : savedStacks) {
                    if (stack.getItem() instanceof VoidRequiemItem) {
                        NbtCompound nbt = stack.getOrCreateNbt();
                        // Штраф: при смерти -20% бонуса
                        nbt.putFloat("DamageBonus", Math.max(nbt.getFloat("DamageBonus") * 0.8f, 0.0f));
                    }
                    newPlayer.getInventory().insertStack(stack);
                }
                newPlayer.sendMessage(Text.translatable("message.beyondthedeep.void_echo").formatted(Formatting.RED), true);
            }
        });
    }
}
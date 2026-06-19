package com.beyondthedeep.event;

import com.beyondthedeep.enchantments.ModEnchantments;
import com.beyondthedeep.items.ModItems;
import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Monster;

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
        registerEnchantmentEffects(); //absorb
        registerRiftEffect(); // rift
        registerArmorPassiveEvents(); // armor
        registerEliteMobs();
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
                // Увеличим радиус и добавим задержку, чтобы лут точно появился
                world.getServer().execute(() -> { // Выполняем в следующем тике
                    Box box = player.getBoundingBox().expand(10.0); // Радиус 10
                    world.getEntitiesByClass(net.minecraft.entity.ItemEntity.class, box, e -> true)
                            .forEach(item -> item.teleport(player.getX(), player.getY(), player.getZ()));
                    world.getEntitiesByClass(net.minecraft.entity.ExperienceOrbEntity.class, box, e -> true)
                            .forEach(orb -> orb.teleport(player.getX(), player.getY(), player.getZ()));
                });

                player.heal(1.0f + (level * 0.5f));
                int duration = 100 + (level * 40);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, Math.min(level / 3, 2)));
            }
        });
    }
    // Добавь эту Map в класс ModEvents или в отдельный менеджер кулдаунов
    private static final Map<UUID, Long> RIFT_COOLDOWNS = new HashMap<>();

    private static void registerRiftEffect() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // Проверка кулдауна
            long currentTime = System.currentTimeMillis();
            long lastUsed = RIFT_COOLDOWNS.getOrDefault(player.getUuid(), 0L);

            if (currentTime - lastUsed < 2000) return ActionResult.PASS;

            ItemStack stack = player.getStackInHand(hand);
            int level = EnchantmentHelper.getLevel(ModEnchantments.RIFT, stack);

            if (level > 0 && world.random.nextFloat() < (0.2f + (level * 0.05f))) {
                RIFT_COOLDOWNS.put(player.getUuid(), currentTime);

                // Урон (используем DamageSources для 1.20.1)
                entity.damage(world.getDamageSources().playerAttack(player), 1.0f + (0.2f * level));

                // Статус эффекты
                if (entity instanceof LivingEntity living) {
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, level * 20, 3));
                }

                player.sendMessage(Text.translatable("message.beyondthedeep.rift_trigger").formatted(Formatting.DARK_PURPLE), true);

                // Визуальные эффекты (для 1.20.1 используем ServerWorld, если нужно спавнить частицы)
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + 1, entity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }
            return ActionResult.PASS;
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
    private static void registerArmorPassiveEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Проходим по всем мирам на сервере
            for (net.minecraft.server.world.ServerWorld world : server.getWorlds()) {
                // Проходим по всем игрокам в этом мире
                for (net.minecraft.server.network.ServerPlayerEntity player : world.getPlayers()) {

                    // Оптимизация: раз в 10 тиков (0.5 сек)
                    if (player.age % 10 != 0) continue;

                    // --- 1. SHADOW TREADS (Ботинки) ---
                    ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
                    if (EnchantmentHelper.getLevel(ModEnchantments.SHADOW_TREADS, boots) > 0) {
                        if (world.getLightLevel(player.getBlockPos()) < 7) {
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 25, 0, true, false, false));
                        }
                    }

                    // --- 2. VOID AURA (Шлем) ---
                    // Здесь будет логика для Void Aura, когда мы сделаем элитных мобов
                }
            }
        });
    }
    public static void registerEliteMobs() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // Проверяем, что это сервер, монстр и не игрок
            if (world.isClient || !(entity instanceof net.minecraft.entity.mob.Monster) || entity instanceof PlayerEntity) {
                return;
            }

            LivingEntity mob = (LivingEntity) entity;
            NbtCompound nbt = new NbtCompound();
            mob.writeNbt(nbt);

            // Если моб уже был проверен (есть тег is_checked), пропускаем
            if (nbt.contains("is_checked")) {
                return;
            }

            // Отмечаем, что моб прошел проверку
            nbt.putBoolean("is_checked", true);

            // 15% шанс стать элитным
            if (world.random.nextFloat() < 0.15f) {
                nbt.putBoolean("is_elite", true);
                mob.readNbt(nbt); // Записываем изменения NBT
                makeElite(mob);
            } else {
                mob.readNbt(nbt); // Просто сохраняем "is_checked", чтобы не проверять снова
            }
        });
    }

    private static void makeElite(LivingEntity mob) {
        // Здоровье х1.75
        mob.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(mob.getMaxHealth() * 1.75);
        mob.setHealth(mob.getMaxHealth());

        // Урон: +2 единицы (1 сердце) к базовому
        var attack = mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attack != null) attack.setBaseValue(attack.getBaseValue() + 4.0);

        // Скорость +20%
        var speed = mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(speed.getBaseValue() * 1.2);

        // Визуал
        mob.setGlowing(true);

        // Эффект: при ударе моб дает игроку замедление (через Mixin или просто пассив)
        // Пока оставим твой Resistance, чтобы он был чуть живучее
        mob.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, -1, 0, false, false, false));
    }
}
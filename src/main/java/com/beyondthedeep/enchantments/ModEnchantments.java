package com.beyondthedeep.enchantments;

import com.beyondthedeep.enchantments.enchants.EchoAccelerationEnchantment;
import com.beyondthedeep.enchantments.enchants.VoidLimitEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    // --- ОСОБЫЕ (Классовые зачарования) ---
    public static final Enchantment VOID_LIMIT = new VoidLimitEnchantment();
    public static final Enchantment ECHO_ACCELERATION = new EchoAccelerationEnchantment();

    // --- ОРУЖИЕ ---
    // ABSORB: Пылесос лута и опыта, хил и сила на высоких уровнях
    public static final Enchantment ABSORB = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, false, 3);
    // RIFT: Доп. урон и оглушение (Slowness) врагов
    public static final Enchantment RIFT = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, false, 3);

    // --- БРОНЯ ---
    // VOID_NULLIFICATION: Активное парирование урона на нагруднике
    public static final Enchantment VOID_NULLIFICATION = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, false, 3);

    // --- ШЛЕМ ---
    // VOID_AURA: Пассивная скрытность, мобы теряют агро на расстоянии > 2 блоков
    public static final Enchantment VOID_AURA = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD}, false, 1);

    // --- БОТИНКИ ---
    // SHADOW_TREADS: Скорость I при низком уровне освещения (без отображения в HUD)
    public static final Enchantment SHADOW_TREADS = new BaseEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, false, 1);

    /**
     * Регистрация всех зачарований мода в реестре игры
     */
    public static void registerModEnchantments() {
        // Оружие
        register("absorb", ABSORB);
        register("rift", RIFT);

        // Броня
        register("void_nullification", VOID_NULLIFICATION);
        register("void_aura", VOID_AURA);
        register("shadow_treads", SHADOW_TREADS);

        // Особые
        register("echo_acceleration", ECHO_ACCELERATION);
        register("void_limit", VOID_LIMIT);
    }

    /**
     * Вспомогательный метод для регистрации в реестре
     */
    private static void register(String name, Enchantment enchantment) {
        Registry.register(Registries.ENCHANTMENT, new Identifier("beyondthedeep", name), enchantment);
    }
}
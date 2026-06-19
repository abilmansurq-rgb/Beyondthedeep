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
    // Регистрация зачарования "Лимит Бездны"
    public static final Enchantment VOID_LIMIT = new VoidLimitEnchantment();
    // Регистрация зачарования "Ускорение Эха"
    public static final Enchantment ECHO_ACCELERATION = new EchoAccelerationEnchantment();

    // base enchantments
// --- ОРУЖИЕ (Универсальные) ---
    public static final Enchantment ABSORB = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, false, 3);
    public static final Enchantment RIFT = new BaseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, false, 3);

    // --- БРОНЯ (Универсальные) ---
    public static final Enchantment VOID_PULL = new BaseEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values(), false, 3);
    public static final Enchantment VOID_STABILITY = new BaseEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values(), false, 3);


    public static void registerModEnchantments() {
        register("absorb", ABSORB);
        register("rift", RIFT);
        register("void_pull", VOID_PULL);
        register("void_stability", VOID_STABILITY);
        register("echo_acceleration", ECHO_ACCELERATION);
        register("void_limit", VOID_LIMIT);
    }

    private static void register(String name, Enchantment enchantment) {
        Registry.register(Registries.ENCHANTMENT, new Identifier("beyondthedeep", name), enchantment);
    }
}
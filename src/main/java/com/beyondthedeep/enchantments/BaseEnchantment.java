package com.beyondthedeep.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class BaseEnchantment extends Enchantment {
    public final int maxLevel;
    public final boolean isTreasure;

    public BaseEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slots, boolean isTreasure, int maxLevel) {
        super(rarity, target, slots);
        this.isTreasure = isTreasure;
        this.maxLevel = maxLevel;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }
}
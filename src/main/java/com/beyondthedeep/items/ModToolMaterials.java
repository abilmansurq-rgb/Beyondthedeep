package com.beyondthedeep.items;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import java.util.function.Supplier;

public class ModToolMaterials {
    public static final ToolMaterial VOID_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() { return 500; }

        @Override
        public float getMiningSpeedMultiplier() { return 4.0F; }

        @Override
        public float getAttackDamage() { return 2.0F; } // Добавим немного урона от материала

        @Override
        public int getMiningLevel() { return 2; }

        @Override
        public int getEnchantability() { return 15; }

        @Override
        public Ingredient getRepairIngredient() {
            // Используем Supplier (через скобки), чтобы избежать ошибки null-ссылки
            return Ingredient.ofItems(ModItems.VOID_ALLOY);
        }
    };
}
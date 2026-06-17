package com.beyondthedeep.items;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ItemStack;

// Мы наследуемся от VoidItem, поэтому все инструменты автоматически получают свечение (hasGlint)
public class VoidTool extends VoidItem {
    private final ToolMaterial material;

    public VoidTool(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(settings);
        this.material = material; // Теперь переменная используется!
    }

    /*
     * МЕСТО ДЛЯ ТВОИХ ИЗМЕНЕНИЙ:
     * Если ты захочешь добавить этому предмету особые свойства при ударе,
     * переопредели метод postHit:
     */
    @Override
    public boolean postHit(ItemStack stack, net.minecraft.entity.LivingEntity target, net.minecraft.entity.LivingEntity attacker) {
        // Здесь можно добавить логику, которая срабатывает при каждом ударе
        // Например: наложение эффекта на моба или проигрывание звука
        return super.postHit(stack, target, attacker);
    }
}
package com.beyondthedeep.items.custom;

import com.beyondthedeep.items.SoulboundItem;
import com.beyondthedeep.enchantments.ModEnchantments; // Убедись, что путь правильный
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class VoidRequiemItem extends SwordItem implements SoulboundItem {
    private static final UUID DAMAGE_BONUS_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public VoidRequiemItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // Новый метод: вычисляет динамический лимит
    public int getVoidLimit(ItemStack stack) {
        int level = EnchantmentHelper.getLevel(ModEnchantments.VOID_LIMIT, stack);
        // Базовый 30, каждый уровень чар добавляет +10
        return 30 + (level * 10);
    }

    // Логика стадий теперь зависит от динамического лимита
    public int getVoidStage(ItemStack stack) {
        float bonus = stack.getOrCreateNbt().getFloat("DamageBonus");
        int limit = getVoidLimit(stack);

        if (bonus >= limit) return 3;
        if (bonus >= (limit * 2 / 3)) return 2;
        if (bonus >= (limit / 3)) return 1;
        return 0;
    }

    // Метод inventoryTick больше не содержит партиклов,
    // но можно оставить его пустым или вообще удалить (если нет других нужд)
    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        // Партиклы удалены для оптимизации
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = super.getAttributeModifiers(stack, slot);

        if (slot == EquipmentSlot.MAINHAND) {
            float bonus = stack.getOrCreateNbt().getFloat("DamageBonus");
            if (bonus <= 0) return modifiers;

            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            modifiers.forEach((attribute, modifier) -> {
                if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
                    builder.put(attribute, new EntityAttributeModifier(
                            modifier.getId(),
                            modifier.getName(),
                            modifier.getValue() + bonus,
                            modifier.getOperation()
                    ));
                } else {
                    builder.put(attribute, modifier);
                }
            });
            return builder.build();
        }
        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable net.minecraft.world.World world, List<Text> tooltip, TooltipContext context) {
        float bonus = stack.getOrCreateNbt().getFloat("DamageBonus");
        int limit = getVoidLimit(stack);

        tooltip.add(Text.translatable("tooltip.beyondthedeep.damage_bonus")
                .append(Text.literal(": " + String.format("%.2f", bonus) + " / " + limit))
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
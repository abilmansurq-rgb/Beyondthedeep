package com.beyondthedeep.screen;

import com.beyondthedeep.blocks.entity.MobFarmBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MobFarmScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    // Этот конструктор жизненно важен для ExtendedScreenHandler
    //public MobFarmScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
      //  this(syncId, playerInventory, (Inventory) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(2));
    //}

    // 1. Для клиента (открытие через Network)
    public MobFarmScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, (Inventory) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(2));
    }

    // 2. Для сервера (основной)
    public MobFarmScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ModScreenHandlers.MOB_FARM_SCREEN_HANDLER, syncId);
        checkSize(inventory, 5); // Проверка размера инвентаря
        this.inventory = inventory;
        this.propertyDelegate = delegate;

        // Добавляем слоты блока (Карточка, Топливо, 3 выхода)
        this.addSlot(new Slot(inventory, 0, 17, 17)); // Карточка
        this.addSlot(new Slot(inventory, 1, 79, 17)); // Топливо
        this.addSlot(new Slot(inventory, 2, 56, 51)); // Выход 1
        this.addSlot(new Slot(inventory, 3, 79, 58)); // Выход 2
        this.addSlot(new Slot(inventory, 4, 102, 51)); // Выход 3

        // Добавляем инвентарь игрока
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // Хотбар игрока
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperties(delegate);
    }

    public PropertyDelegate getDelegate() { return this.propertyDelegate; }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // Если кликнули в слоты блока (0-4)
            if (invSlot < 5) {
                if (!this.insertItem(originalStack, 5, 41, true)) return ItemStack.EMPTY;
            }
            // Если кликнули в инвентарь игрока (5-41)
            else if (!this.insertItem(originalStack, 0, 5, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return newStack;
    }
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
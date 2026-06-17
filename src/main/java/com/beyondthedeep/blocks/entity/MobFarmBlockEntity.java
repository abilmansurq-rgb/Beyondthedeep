package com.beyondthedeep.blocks.entity;

import com.beyondthedeep.blocks.ModBlockEntities;
import com.beyondthedeep.screen.MobFarmScreenHandler;
import com.beyondthedeep.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MobFarmBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    // Инвентарь на 5 слотов
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    // Добавь этот метод в MobFarmBlockEntity
    @Override
    public void writeScreenOpeningData(net.minecraft.server.network.ServerPlayerEntity player, net.minecraft.network.PacketByteBuf buf) {
        buf.writeBlockPos(this.pos); // Передаем позицию блока в GUI
    }
    private static final net.minecraft.registry.tag.TagKey<net.minecraft.item.Item> FUEL_TAG =
            net.minecraft.registry.tag.TagKey.of(net.minecraft.registry.RegistryKeys.ITEM, new net.minecraft.util.Identifier("beyondthedeep", "mob_farm_fuel"));
    private int progress = 0;
    private int fuelLeft = 0; // Добавь эту переменную в начало класса, если её ещё нет

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> MobFarmBlockEntity.this.progress;
                case 1 -> MobFarmBlockEntity.this.fuelLeft;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> MobFarmBlockEntity.this.progress = value;
                case 1 -> MobFarmBlockEntity.this.fuelLeft = value;
            }
        }
        @Override public int size() { return 2; }
    };
    public MobFarmBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_FARM_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() { return inventory; }

    @Override
    public Text getDisplayName() { return Text.translatable("guiName.beyondthedeep.mobFarm"); }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MobFarmScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("mob_farm.progress", progress);
        nbt.putInt("mob_farm.fuelLeft", fuelLeft); // ДОБАВЬ ЭТУ СТРОКУ
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("mob_farm.progress");
        fuelLeft = nbt.getInt("mob_farm.fuelLeft"); // ДОБАВЬ ЭТУ СТРОКУ
    }
    public static void tick(net.minecraft.world.World world, BlockPos pos, BlockState state, MobFarmBlockEntity entity) {
        if (world.isClient) return;

        // Слот 1 — это слот топлива
        ItemStack fuelStack = entity.inventory.get(1);

        // Если топлива нет, но в слоте лежит подходящий предмет — заправляем
        if (entity.fuelLeft <= 0 && fuelStack.isIn(FUEL_TAG)) {
            fuelStack.decrement(1); // Забираем 1 предмет
            entity.fuelLeft = 1600; // Даем 1600 единиц энергии (16 циклов)
        }

        // Если есть энергия — работаем
        if (entity.fuelLeft > 0) {
            // Проверяем слот 0 (например, "карточка" или ресурс для переработки)
            if (!entity.inventory.get(0).isEmpty()) {
                entity.progress++;
                entity.fuelLeft--; // Расходуем энергию каждый тик

                if (entity.progress >= 100) {
                    entity.progress = 0;
                    // TODO: Здесь будет логика спавна дропа
                }
            }
        }

        markDirty(world, pos, state);
    }
}
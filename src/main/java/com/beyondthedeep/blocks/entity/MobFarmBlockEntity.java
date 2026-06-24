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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

    // ... внутри MobFarmBlockEntity.java ...

    private void spawnMobDrop() {
        ItemStack cardStack = this.inventory.get(0);

        // 1. Проверяем NBT и наличие нашего ключа
        if (cardStack.hasNbt() && cardStack.getNbt().contains("MobName")) {
            // 2. Достаем строку-ID (например, "minecraft:zombie")
            String mobIdString = cardStack.getNbt().getString("MobName");
            Identifier mobId = new Identifier(mobIdString); // Создаем объект Identifier

            // 3. Получаем дроп (теперь используем метод с Identifier)
            ItemStack loot = getLootForMob(mobId);

            // ... логика слотов остается без изменений ...
            for (int i = 2; i <= 4; i++) {
                ItemStack slotStack = this.inventory.get(i);
                if (slotStack.isEmpty()) {
                    this.inventory.set(i, loot);
                    return;
                } else if (ItemStack.canCombine(slotStack, loot) && slotStack.getCount() < slotStack.getMaxCount()) {
                    slotStack.increment(loot.getCount());
                    return;
                }
            }
        }
    }

    // 4. НОВЫЙ МЕТОД ПОИСКА ЛУТА
    private ItemStack getLootForMob(Identifier mobId) {
        // Используем toString() ID для проверки. Это надежно на 100%
        String id = mobId.toString();

        // Используем switch, который работает по ID сущности
        return switch (id) {
            case "minecraft:slime" -> new ItemStack(net.minecraft.item.Items.SLIME_BALL, 2);
            case "minecraft:wither_skeleton" -> new ItemStack(net.minecraft.item.Items.COAL, 1);
            case "minecraft:zombie" -> new ItemStack(net.minecraft.item.Items.ROTTEN_FLESH, 2);

            // Ты можешь легко добавить мобов из других модов:
            // case "someothermod:boss" -> new ItemStack(ModItems.VOID_SHARD, 1);

            default -> new ItemStack(net.minecraft.item.Items.DIRT, 1); // "Заглушка" на случай, если моб не прописан
        };
    }

    public static void tick(net.minecraft.world.World world, BlockPos pos, BlockState state, MobFarmBlockEntity entity) {
        if (world.isClient) return;

        ItemStack fuelStack = entity.inventory.get(1);
        ItemStack cardStack = entity.inventory.get(0);

        // ДИАГНОСТИКА: выводим состояние слотов
        // (убери этот вывод после того, как поймешь причину)
        //if (entity.progress % 20 == 0) { // Выводить раз в секунду, чтобы не спамить
           // System.out.println("Топливо: " + entity.fuelLeft + ", Карта имеет NBT: " + cardStack.hasNbt());
       // }

        // Блок заправки
        if (entity.fuelLeft <= 0 && fuelStack.isOf(com.beyondthedeep.items.ModItems.VOID_SHARD)) {
            fuelStack.decrement(1);
            entity.fuelLeft = 1600;
            //System.out.println("Заправлено!");
        }

        // Блок работы
        if (entity.fuelLeft > 0) {
            if (cardStack.hasNbt() && cardStack.getNbt().contains("MobName")) {
                entity.progress++;
                entity.fuelLeft--;
                if (entity.progress >= 100) {
                    entity.progress = 0;
                    entity.spawnMobDrop();
                }
            }
        }
        markDirty(world, pos, state);
    }
}
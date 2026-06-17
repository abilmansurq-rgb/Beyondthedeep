package com.beyondthedeep.blocks;

import com.beyondthedeep.BeyondTheDeep;
import com.beyondthedeep.blocks.entity.MobFarmBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<MobFarmBlockEntity> MOB_FARM_BLOCK_ENTITY;

    public static void registerAllBlockEntities() {
        MOB_FARM_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier("beyondthedeep", "mob_farm_be"),
                FabricBlockEntityTypeBuilder.create(MobFarmBlockEntity::new,
                        // СЮДА НУЖНО БУДЕТ ДОБАВИТЬ САМ БЛОК (который мы создадим в п.2)
                        ModBlocks.MOB_FARM_BLOCK).build());
        BeyondTheDeep.LOGGER.info("Successfully registered Block Entities for Beyond The Deep.");
    }
}
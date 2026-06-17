package com.beyondthedeep.screen;

import com.beyondthedeep.BeyondTheDeep;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<MobFarmScreenHandler> MOB_FARM_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        MOB_FARM_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER,
                new Identifier("beyondthedeep", "mob_farm_handler"),
                new ExtendedScreenHandlerType<>(MobFarmScreenHandler::new));
    }
}
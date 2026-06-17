package com.beyondthedeep;

import com.beyondthedeep.screen.ModScreenHandlers;
import com.beyondthedeep.screen.MobFarmScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry; // В новых версиях: HandledScreens

public class BeyondTheDeepClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// РЕГИСТРАЦИЯ ЗДЕСЬ:
		// Используем HandledScreens для 1.20.1+
		net.minecraft.client.gui.screen.ingame.HandledScreens.register(
				ModScreenHandlers.MOB_FARM_SCREEN_HANDLER,
				MobFarmScreen::new
		);
	}
}
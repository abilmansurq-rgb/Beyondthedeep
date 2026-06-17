package com.beyondthedeep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;

public class MobFarmScreen extends HandledScreen<MobFarmScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("beyondthedeep", "textures/gui/mob_farm_gui.png");

    public MobFarmScreen(MobFarmScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 176; // Ставь свое значение высоты здесь!
        this.backgroundWidth = 176;  // Ставь свою ширину
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // --- ДОБАВЛЯЕМ БАРЫ ---

        // Получаем значения из хендлера (0 - прогресс, 1 - топливо)
        int progress = this.handler.getDelegate().get(0);
        int fuel = this.handler.getDelegate().get(1);

        // Рисуем бар прогресса (красный)
        // x + 80, y + 30 — это координаты слота карточки (поправь под свои нужды)
        int progressWidth = (progress * 24) / 100; // Если прогресс 0-100
        context.fill(x + 80, y + 30, x + 80 + progressWidth, y + 35, 0xFFFF0000);

        // Рисуем бар топлива (синий)
        // x + 80, y + 60 — это координаты слота топлива
        int fuelWidth = (fuel * 24) / 1600; // 1600 — наш максимум топлива
        context.fill(x + 80, y + 60, x + 80 + fuelWidth, y + 65, 0xFF0000FF);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
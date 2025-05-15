package com.example.craftmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class CustomCraftingScreen extends Screen {

    public CustomCraftingScreen() {
        super(new StringTextComponent("カスタムクラフト"));
    }

    @Override
    protected void init() {
        // 初期化（今は空）
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "カスタムクラフト画面", this.width / 2, 20, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}

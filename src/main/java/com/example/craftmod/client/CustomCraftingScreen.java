package com.example.craftmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class CustomCraftingScreen extends Screen {

    private final List<String> dummyRecipes = new ArrayList<>();
    private int scrollOffset = 0;
    private int selectedIndex = -1;

    private static final int ENTRIES_PER_PAGE = 6;

    public CustomCraftingScreen() {
        super(new StringTextComponent("カスタムクラフト"));
    }

    @Override
    protected void init() {
        dummyRecipes.clear();
        dummyRecipes.add("弓");
        dummyRecipes.add("木の剣");
        dummyRecipes.add("石のつるはし");
        dummyRecipes.add("かまど");
        dummyRecipes.add("ベッド");
        dummyRecipes.add("トーチ");
        dummyRecipes.add("ドア");
        dummyRecipes.add("階段");
        dummyRecipes.add("チェスト");
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        int maxScroll = Math.max(0, dummyRecipes.size() - ENTRIES_PER_PAGE);
        scrollOffset -= (int) Math.signum(scrollDelta);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = this.width / 4;
        int startY = 40;

        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            int recipeIndex = i + scrollOffset;
            if (recipeIndex >= dummyRecipes.size()) break;

            int entryY = startY + i * 20;
            if (mouseX >= startX && mouseX <= startX + 100 && mouseY >= entryY && mouseY <= entryY + 16) {
                selectedIndex = recipeIndex;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        drawCenteredString(matrixStack, font, "カスタムクラフト画面", this.width / 2, 10, 0xFFFFFF);

        // 左側：レシピ一覧
        int startX = this.width / 4;
        int startY = 40;

        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            int recipeIndex = i + scrollOffset;
            if (recipeIndex >= dummyRecipes.size()) break;

            String name = dummyRecipes.get(recipeIndex);
            int entryY = startY + i * 20;
            int color = (recipeIndex == selectedIndex) ? 0x00FF00 : 0xFFFFFF;

            drawString(matrixStack, font, "- " + name, startX, entryY, color);
        }

        // 右側：選択中のレシピ詳細（仮）
        int rightX = this.width * 3 / 5;
        if (selectedIndex >= 0 && selectedIndex < dummyRecipes.size()) {
            String selected = dummyRecipes.get(selectedIndex);
            drawString(matrixStack, font, "選択中: " + selected, rightX, 40, 0xFFFF00);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}

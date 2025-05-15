package com.example.craftmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;


import java.util.ArrayList;
import java.util.List;

public class CustomCraftingScreen extends Screen {

    private final List<String> dummyRecipes = new ArrayList<>();
    private int scrollOffset = 0;
    private int selectedIndex = -1;

    private static final int ENTRIES_PER_PAGE = 6;

    private int craftAmount = 1;
    private Button minusButton;
    private Button plusButton;
    private Button craftButton;
    private TextFieldWidget amountInput;

    public CustomCraftingScreen() {
        super(new StringTextComponent("カスタムクラフト"));
    }

    @Override
    protected void init() {
        // ダミーレシピ（省略）

        int rightX = this.width * 3 / 5;
        int y = 80;

        // 数値入力欄
        amountInput = new TextFieldWidget(this.font, rightX + 40, y, 30, 20, StringTextComponent.EMPTY);
        amountInput.setValue("1");
        amountInput.setResponder(val -> {
            try {
                int n = Integer.parseInt(val);
                craftAmount = Math.max(1, Math.min(n, 99));
            } catch (NumberFormatException e) {
                craftAmount = 1;
            }
        });
        this.addButton(amountInput);

        // ＋ ボタン
        plusButton = new Button(rightX + 75, y, 20, 20, new StringTextComponent("+"), b -> {
            craftAmount = Math.min(craftAmount + 1, 99);
            amountInput.setValue(String.valueOf(craftAmount));
        });
        this.addButton(plusButton);

        // − ボタン
        minusButton = new Button(rightX + 10, y, 20, 20, new StringTextComponent("-"), b -> {
            craftAmount = Math.max(craftAmount - 1, 1);
            amountInput.setValue(String.valueOf(craftAmount));
        });
        this.addButton(minusButton);

        // 作成するボタン（今は動作しない）
        craftButton = new Button(rightX + 10, y + 30, 80, 20, new StringTextComponent("作成する"), b -> {
            // TODO: 次ステップで素材消費＋アイテム作成処理
        });
        this.addButton(craftButton);
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
            drawString(matrixStack, font, "素材: 糸 0/3  棒 13/3", rightX, 60, 0xFFFFFF);
            drawString(matrixStack, font, "制作個数: ", rightX, 85, 0xFFFFFF);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}

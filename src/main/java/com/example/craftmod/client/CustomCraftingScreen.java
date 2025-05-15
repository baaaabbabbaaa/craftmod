package com.example.craftmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import java.util.Map;
import java.util.HashMap;


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

    private final Map<Item, Integer> requiredMaterials = new HashMap<>();

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
            boolean canCraft = true;

            // 素材足りてるかチェック
            for (Map.Entry<Item, Integer> entry : requiredMaterials.entrySet()) {
                Item item = entry.getKey();
                int required = entry.getValue() * craftAmount;
                int owned = minecraft.player.inventory.countItem(item);
                if (owned < required) {
                    canCraft = false;
                    break;
                }
            }

            if (!canCraft) {
                minecraft.player.displayClientMessage(new StringTextComponent("素材が足りません！"), true);
                return;
            }

            // 素材消費処理
            for (Map.Entry<Item, Integer> entry : requiredMaterials.entrySet()) {
                Item item = entry.getKey();
                int toRemove = entry.getValue() * craftAmount;

                for (int i = 0; i < minecraft.player.inventory.getContainerSize(); i++) {
                    if (toRemove <= 0) break;
                    ItemStack stack = minecraft.player.inventory.getItem(i);
                    if (stack.getItem() == item) {
                        int remove = Math.min(stack.getCount(), toRemove);
                        stack.shrink(remove);
                        toRemove -= remove;
                    }
                }
            }

            // 完成品追加（弓）
            ItemStack result = new ItemStack(Items.BOW, craftAmount);
            minecraft.player.inventory.add(result);

            // 成功メッセージ
            minecraft.player.displayClientMessage(new StringTextComponent("弓を " + craftAmount + " 個作成しました！"), true);
        });

        this.addButton(craftButton);

        requiredMaterials.clear();
        requiredMaterials.put(Items.STRING, 3);
        requiredMaterials.put(Items.STICK, 3);
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

            int y = 60;
            for (Map.Entry<Item, Integer> entry : requiredMaterials.entrySet()) {
                Item item = entry.getKey();
                int required = entry.getValue() * craftAmount;
                int owned = minecraft.player.inventory.countItem(item);

                int color = owned >= required ? 0x00FF00 : 0xFF4444;
                font.draw(matrixStack,
                        new ItemStack(item).getHoverName().getString() + " " + owned + "/" + required,
                        rightX, y, color
                );
                y += 12;
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}

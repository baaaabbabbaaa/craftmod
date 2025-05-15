package com.example.craftmod;

import com.example.craftmod.client.ModScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("craftmod") // ← ここは mods.toml の modId と合わせて！
public class MainMod {

    public MainMod() {
        // Forgeのイベントバスにクライアント側の画面登録クラスを登録する
        MinecraftForge.EVENT_BUS.register(ModScreens.class);
    }
}

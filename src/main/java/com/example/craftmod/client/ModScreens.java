package com.example.craftmod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "craftmod", value = Dist.CLIENT)
public class ModScreens {

    public static void openCraftingScreen() {
        Minecraft.getInstance().setScreen(new CustomCraftingScreen());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                net.minecraft.command.Commands.literal("opencraftscreen")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            Minecraft.getInstance().tell(ModScreens::openCraftingScreen);
                            return 1;
                        })
        );
    }
}

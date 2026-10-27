package com.pvptraining;

import com.pvptraining.bot.EntityTrainingBot;
import com.pvptraining.client.RenderTrainingBotFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTrainingBot.class, new RenderTrainingBotFactory());
    }
}

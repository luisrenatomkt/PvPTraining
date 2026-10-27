package com.pvptraining.client;

import com.pvptraining.bot.EntityTrainingBot;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/** Renderiza o bot como um humanoide (textura padrao do Steve; armadura e item na mao sao renderizados pelo RenderBiped). */
public class RenderTrainingBotFactory implements IRenderFactory<EntityTrainingBot> {
    @Override
    public Render<? super EntityTrainingBot> createRenderFor(RenderManager manager) {
        return new RenderBiped<EntityTrainingBot>(manager, new ModelBiped(0.0F), 0.5F);
    }
}

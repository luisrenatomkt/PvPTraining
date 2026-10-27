package com.pvptraining;

import com.pvptraining.bot.EntityTrainingBot;
import com.pvptraining.recraft.SoupHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = PvPTraining.MODID, name = "PvP Training", version = "0.1.0", acceptedMinecraftVersions = "[1.8.9]")
public class PvPTraining {
    public static final String MODID = "pvptraining";

    @Mod.Instance(MODID)
    public static PvPTraining instance;

    @SidedProxy(clientSide = "com.pvptraining.ClientProxy", serverSide = "com.pvptraining.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // id 0, alcance de tracking 64, atualiza a cada 1 tick, envia velocidade
        EntityRegistry.registerModEntity(EntityTrainingBot.class, "TrainingBot", 0, this, 64, 1, true);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new SoupHandler());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPvPT());
    }
}

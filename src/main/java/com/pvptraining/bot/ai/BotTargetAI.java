package com.pvptraining.bot.ai;

import com.pvptraining.bot.EntityTrainingBot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/** Selecao de alvo: jogador vivo mais proximo dentro do alcance. Ignora criativo/espectador (getClosestPlayerToEntity ja filtra). */
public class BotTargetAI extends EntityAIBase {
    private final EntityTrainingBot bot;

    public BotTargetAI(EntityTrainingBot bot) {
        this.bot = bot;
        setMutexBits(1);
    }

    private double range() { return bot.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue(); }

    @Override public boolean shouldExecute() { return bot.worldObj.getClosestPlayerToEntity(bot, range()) != null; }

    @Override public void startExecuting() {
        EntityPlayer p = bot.worldObj.getClosestPlayerToEntity(bot, range());
        bot.setAttackTarget(p);
    }

    @Override public boolean continueExecuting() {
        EntityLivingBase t = bot.getAttackTarget();
        return t != null && t.isEntityAlive() && bot.getDistanceToEntity(t) <= range() * 1.5;
    }

    @Override public void resetTask() { bot.setAttackTarget(null); }
}

package com.pvptraining.bot.ai;

import com.pvptraining.bot.BotProfile;
import com.pvptraining.bot.EntityTrainingBot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;

/**
 * Combate corpo a corpo. Regras: so ataca dentro do alcance E com linha de visao (nao ataca atraves de paredes),
 * respeita intervalo minimo entre golpes (com jitter) e uma chance de acerto por perfil.
 * O knockback vem do proprio attackEntityFrom do vanilla 1.8.9; nao ha knockback extra de sprint.
 */
public class BotCombatAI extends EntityAIBase {
    private final EntityTrainingBot bot;
    private int cooldown;

    public BotCombatAI(EntityTrainingBot bot) {
        this.bot = bot;
        setMutexBits(0); // roda em paralelo ao movimento
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase t = bot.getAttackTarget();
        return t != null && t.isEntityAlive();
    }

    @Override public boolean continueExecuting() { return shouldExecute(); }

    @Override public void updateTask() {
        if (cooldown > 0) cooldown--;
        EntityLivingBase t = bot.getAttackTarget();
        if (t == null || bot.isHealing() || cooldown > 0) return;
        BotProfile p = bot.getProfile();
        if (bot.getDistanceToEntity(t) > p.reach || !bot.canEntityBeSeen(t)) return;

        bot.swingItem();
        if (bot.getRNG().nextDouble() < p.hitChance) {
            t.attackEntityFrom(DamageSource.causeMobDamage(bot), (float) p.damage);
        }
        cooldown = p.attackInterval + bot.getRNG().nextInt(Math.max(1, p.attackJitter + 1));
    }
}

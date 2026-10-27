package com.pvptraining.bot.ai;

import com.pvptraining.bot.BotProfile;
import com.pvptraining.bot.EntityTrainingBot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Movimento: persegue via pathfinding quando longe; perto do alvo faz strafe lateral (se o perfil permitir)
 * e recua levemente se estiver colado. Inclui deteccao simples de "preso" (sem progresso por ~1s).
 * O strafe e aplicado diretamente em motionX/Z (vanilla 1.8.9 nao tem helper de strafe para mobs).
 */
public class BotMoveAI extends EntityAIBase {
    private final EntityTrainingBot bot;
    private int repathTicks, strafeTicks, strafeDir = 1, stuckCheck, stuckCount;
    private double lastX, lastZ;

    public BotMoveAI(EntityTrainingBot bot) {
        this.bot = bot;
        setMutexBits(1);
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase t = bot.getAttackTarget();
        return t != null && t.isEntityAlive();
    }

    @Override public boolean continueExecuting() { return shouldExecute(); }

    @Override public void startExecuting() { lastX = bot.posX; lastZ = bot.posZ; stuckCheck = 0; stuckCount = 0; }

    @Override public void resetTask() { bot.getNavigator().clearPathEntity(); }

    @Override public void updateTask() {
        EntityLivingBase t = bot.getAttackTarget();
        if (t == null) return;
        BotProfile p = bot.getProfile();
        bot.getLookHelper().setLookPositionWithEntity(t, 30.0F, 30.0F);
        double dist = bot.getDistanceToEntity(t);

        if (bot.isHealing()) { // parado "tomando sopa"
            bot.getNavigator().clearPathEntity();
            return;
        }

        if (dist > p.preferredRange) {
            if (--repathTicks <= 0) {
                repathTicks = 6 + bot.getRNG().nextInt(5);
                bot.getNavigator().tryMoveToEntityLiving(t, p.moveSpeed);
            }
        } else {
            bot.getNavigator().clearPathEntity();
            double dx = t.posX - bot.posX, dz = t.posZ - bot.posZ;
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 1.0E-4 && bot.onGround) {
                dx /= len; dz /= len;
                double ax = 0, az = 0;
                if (p.strafes) {
                    if (--strafeTicks <= 0) {
                        strafeTicks = p.strafeFlipMin + bot.getRNG().nextInt(Math.max(1, p.strafeFlipMax - p.strafeFlipMin + 1));
                        strafeDir = bot.getRNG().nextBoolean() ? 1 : -1;
                    }
                    ax += -dz * strafeDir * 0.045; az += dx * strafeDir * 0.045;
                }
                if (dist < p.preferredRange - 0.7) { ax -= dx * 0.03; az -= dz * 0.03; } // recua se colado
                bot.motionX += ax; bot.motionZ += az;
                double sp = Math.sqrt(bot.motionX * bot.motionX + bot.motionZ * bot.motionZ);
                if (sp > 0.22) { bot.motionX *= 0.22 / sp; bot.motionZ *= 0.22 / sp; }
            }
        }

        // deteccao de "preso": a cada 20 ticks verifica se andou algo enquanto deveria perseguir
        if (++stuckCheck >= 20) {
            stuckCheck = 0;
            double moved = Math.hypot(bot.posX - lastX, bot.posZ - lastZ);
            lastX = bot.posX; lastZ = bot.posZ;
            if (dist > p.preferredRange + 0.5 && moved < 0.15) {
                stuckCount++;
                bot.getJumpHelper().setJumping();
                if (stuckCount >= 2) { // tenta outro caminho + empurrao lateral aleatorio
                    bot.getNavigator().clearPathEntity();
                    repathTicks = 0;
                    bot.motionX += (bot.getRNG().nextDouble() - 0.5) * 0.4;
                    bot.motionZ += (bot.getRNG().nextDouble() - 0.5) * 0.4;
                    stuckCount = 0;
                }
            } else stuckCount = 0;
        }
        if (bot.isCollidedHorizontally && bot.onGround) bot.getJumpHelper().setJumping();
    }
}

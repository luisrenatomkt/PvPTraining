package com.pvptraining.bot;

import com.pvptraining.bot.ai.BotCombatAI;
import com.pvptraining.bot.ai.BotMoveAI;
import com.pvptraining.bot.ai.BotTargetAI;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/** Bot de treinamento: entidade humanoide com IA por estados simples (alvo / movimento / combate / cura). */
public class EntityTrainingBot extends EntityCreature {
    public static final float SOUP_HEAL = 7.0F; // 3,5 coracoes, como no Soup PvP

    private BotProfile profile = BotProfile.AGGRESSIVE;
    private int soups;
    /** -1 = nao esta tomando sopa; >=0 = ticks restantes ate a cura surtir efeito. */
    private int healDelay = -1;

    public EntityTrainingBot(World world) {
        super(world);
        setSize(0.6F, 1.8F);
        this.experienceValue = 0;
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new BotMoveAI(this));
        this.tasks.addTask(3, new BotCombatAI(this));
        this.targetTasks.addTask(1, new BotTargetAI(this));
        applyProfile(profile);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    public BotProfile getProfile() { return profile; }
    public int getSoups() { return soups; }
    /** true enquanto o bot "esta tomando sopa" (nao ataca nesse periodo). */
    public boolean isHealing() { return healDelay >= 0; }

    public void setProfile(BotProfile p) { applyProfile(p); }

    private void applyProfile(BotProfile p) {
        this.profile = p;
        this.soups = p.soups;
        this.healDelay = -1;
        setCustomNameTag("\u00a7c[Bot] " + p.displayName);
        setAlwaysRenderNameTag(true);
        // slots: 0 = mao, 1 = botas, 2 = calcas, 3 = peitoral, 4 = capacete
        setCurrentItemOrArmor(0, new ItemStack(p == BotProfile.BEGINNER ? Items.stone_sword : Items.diamond_sword));
        boolean light = (p == BotProfile.BEGINNER);
        setCurrentItemOrArmor(1, new ItemStack(light ? Items.leather_boots : Items.iron_boots));
        setCurrentItemOrArmor(2, new ItemStack(light ? Items.leather_leggings : Items.iron_leggings));
        setCurrentItemOrArmor(3, new ItemStack(light ? Items.leather_chestplate : Items.iron_chestplate));
        setCurrentItemOrArmor(4, new ItemStack(light ? Items.leather_helmet : Items.iron_helmet));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!worldObj.isRemote) tickHealing();
    }

    /** Logica de cura isolada: decide quando tomar sopa e aplica a cura apos o tempo de reacao. */
    private void tickHealing() {
        if (healDelay > 0) {
            healDelay--;
        } else if (healDelay == 0) {
            heal(SOUP_HEAL);
            soups--;
            playSound("random.eat", 1.0F, 1.0F);
            healDelay = -1;
        } else if (soups > 0 && getHealth() <= getMaxHealth() * profile.soupThreshold) {
            healDelay = profile.soupReaction;
            if (healDelay == 0) healDelay = 1;
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!worldObj.isRemote && cause != null && cause.getEntity() instanceof EntityPlayer) {
            ((EntityPlayer) cause.getEntity()).addChatMessage(
                new ChatComponentText("\u00a7aVitoria! Voce derrotou o bot " + profile.displayName + "."));
        }
    }

    @Override protected boolean canDespawn() { return false; }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("BotProfile", profile.name());
        tag.setInteger("BotSoups", soups);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        BotProfile p = BotProfile.fromName(tag.getString("BotProfile"));
        if (p != null) applyProfile(p);
        if (tag.hasKey("BotSoups")) soups = tag.getInteger("BotSoups");
    }
}

package com.pvptraining.recraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Soup PvP: clique direito com sopa de cogumelo cura 7 de vida (3,5 coracoes) e a sopa vira pote, mesmo sem fome. */
public class SoupHandler {
    public static final float SOUP_HEAL = 7.0F;

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.world.isRemote) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
            && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        EntityPlayer player = event.entityPlayer;
        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() != Items.mushroom_stew) return;
        if (player.getHealth() >= player.getMaxHealth()) return; // so consome se precisar curar

        event.setCanceled(true);
        player.heal(SOUP_HEAL);
        player.worldObj.playSoundAtEntity(player, "random.eat", 1.0F, 1.0F);
        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bowl));
        player.inventoryContainer.detectAndSendChanges();
    }
}

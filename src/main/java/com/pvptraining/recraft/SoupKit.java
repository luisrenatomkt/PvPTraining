package com.pvptraining.recraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/** Kit de Soup PvP: espada, armadura de ferro e sopas (configuravel pelo numero de sopas). */
public class SoupKit {
    public static void give(EntityPlayer p, int soups) {
        p.inventory.clear();
        p.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond_sword));
        int slot = 1;
        for (int i = 0; i < soups && slot < 36; i++, slot++) {
            p.inventory.setInventorySlotContents(slot, new ItemStack(Items.mushroom_stew));
        }
        // armadura: indice 0 = botas ... 3 = capacete
        p.inventory.armorInventory[0] = new ItemStack(Items.iron_boots);
        p.inventory.armorInventory[1] = new ItemStack(Items.iron_leggings);
        p.inventory.armorInventory[2] = new ItemStack(Items.iron_chestplate);
        p.inventory.armorInventory[3] = new ItemStack(Items.iron_helmet);
        p.setHealth(p.getMaxHealth());
        p.getFoodStats().addStats(20, 1.0F);
        p.inventoryContainer.detectAndSendChanges();
    }
}

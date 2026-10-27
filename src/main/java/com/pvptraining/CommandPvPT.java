package com.pvptraining;

import com.pvptraining.bot.BotProfile;
import com.pvptraining.bot.EntityTrainingBot;
import com.pvptraining.recraft.SoupKit;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** /pvpt spawn [perfil] | kit [sopas] | clear. Comandos temporarios ate o menu (Fase 6). */
public class CommandPvPT extends CommandBase {
    @Override public String getCommandName() { return "pvpt"; }
    @Override public String getCommandUsage(ICommandSender s) { return "/pvpt <spawn [perfil] | kit [sopas] | clear>"; }
    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        if (args.length == 0) { msg(sender, getCommandUsage(sender)); return; }
        switch (args[0].toLowerCase()) {
            case "spawn": {
                BotProfile prof = BotProfile.AGGRESSIVE;
                if (args.length > 1) {
                    prof = BotProfile.fromName(args[1]);
                    if (prof == null) { msg(sender, "Perfil invalido. Use: " + Arrays.toString(BotProfile.values())); return; }
                }
                EntityTrainingBot bot = new EntityTrainingBot(player.worldObj);
                double rad = Math.toRadians(player.rotationYaw);
                bot.setLocationAndAngles(player.posX - Math.sin(rad) * 4, player.posY, player.posZ + Math.cos(rad) * 4,
                    player.rotationYaw + 180, 0);
                bot.setProfile(prof);
                player.worldObj.spawnEntityInWorld(bot);
                msg(sender, "Bot " + prof.displayName + " criado.");
                break;
            }
            case "kit": {
                int soups = 20;
                if (args.length > 1) soups = parseInt(args[1], 0, 34);
                SoupKit.give(player, soups);
                msg(sender, "Kit de Soup PvP entregue (" + soups + " sopas).");
                break;
            }
            case "clear": {
                int n = 0;
                for (Object o : new ArrayList<Object>(player.worldObj.loadedEntityList)) {
                    if (o instanceof EntityTrainingBot) { ((Entity) o).setDead(); n++; }
                }
                msg(sender, n + " bot(s) removido(s).");
                break;
            }
            default: msg(sender, getCommandUsage(sender));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "spawn", "kit", "clear");
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            List<String> names = new ArrayList<String>();
            for (BotProfile p : BotProfile.values()) names.add(p.name().toLowerCase());
            return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
        }
        return null;
    }

    private static void msg(ICommandSender s, String text) { s.addChatMessage(new ChatComponentText(text)); }
}

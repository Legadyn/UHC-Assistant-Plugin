package me.legadyn.uhcscoreboard.teams;

import me.legadyn.uhcscoreboard.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamsCommandExecutor implements CommandExecutor {
    Main plugin;

    Teams teams;

    String prefix = ChatColor.translateAlternateColorCodes('&', "&f&l[&6&lUHC&f&l] &r");

    public TeamsCommandExecutor(Main plugin) {
        this.plugin = plugin;
        this.teams = new Teams(plugin);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player)sender;
        try {
            if (label.equalsIgnoreCase("uhcteams") && args.length >= 1) {
                if (!player.isOp() && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("deleteall") || args[0].equalsIgnoreCase("leaveall") || args[0].equalsIgnoreCase("allowfriendly"))) {
                    player.sendMessage(this.prefix + ChatColor.RED + "You don't have permission to do this");
                    return true;
                }
                if (args[0].equalsIgnoreCase("create") && args.length <= 2) {
                    this.teams.createTeam(args[1], player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("join") && args.length <= 2) {
                    this.teams.joinTeam(args[1], player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("delete") && player.isOp() && args.length <= 2) {
                    this.teams.deleteTeam(args[1], player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("deleteall") && player.isOp() && args.length <= 1) {
                    this.teams.deleteAll();
                    player.sendMessage(this.prefix + "All the teams have been deleted");
                    return true;
                }
                if (args[0].equalsIgnoreCase("leave") && args.length <= 1) {
                    this.teams.leaveTeam(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("leaveall") && args.length <= 1 && player.isOp()) {
                    for (String s : this.teams.teams)
                        this.teams.cleanTeam(s);
                    player.sendMessage(this.prefix + "All the players have left their teams");
                    return true;
                }
                if (args[0].equalsIgnoreCase("list") && args.length <= 1 && player.isOp()) {
                    if (this.teams.teams.isEmpty()) {
                        player.sendMessage(this.prefix + "No teams are created in this server");
                        return true;
                    }
                    String string = this.prefix + "  ";
                    for (String s : this.teams.teams)
                        string = string + s + "&r: " + this.teams.getPlayersNameByTeam(s) + ", ";
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                    return true;
                }
                if (args[0].equalsIgnoreCase("playerlist") && args.length <= 1 && player.isOp()) {
                    player.sendMessage(this.prefix + "Online players list with their respective teams:");
                    for (Player p : Bukkit.getOnlinePlayers())
                        player.sendMessage("-" + p.getName() + ": " + this.teams.getTeam(p, false));
                    return true;
                }
                if (args[0].equalsIgnoreCase("allowfriendlyfire") && isBoolean(args[1]) && args.length <= 2 && player.isOp()) {
                    this.teams.setAllowFriendly(args[1]);
                    player.sendMessage(this.prefix + "Team friendly fire is now set to: " + args[1]);
                    return true;
                }
                if (!player.isOp()) {
                    player.sendMessage(this.prefix + ChatColor.RED + "You don't have permission to do thisa!");
                    return true;
                }
                player.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcteams for view the allowed commands");
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            player.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcteams for view the allowed commands");
            return true;
        }
        player.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcteams for view the allowed commands");
        return true;
    }

    private boolean isBoolean(String string) {
        return (string.equals("false") || string.equals("true"));
    }
}


package me.legadyn.uhcscoreboard;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreboardCommandExecutor implements CommandExecutor {
    Main plugin;

    String prefix = ChatColor.translateAlternateColorCodes('&', "&f&l[&6&lUHC&f&l] &r");

    public ScoreboardCommandExecutor(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player p = (Player)sender;
        if (!p.isOp()) {
            sender.sendMessage(this.prefix + ChatColor.RED + "You don't have permission to do this");
            return true;
        }
        try {
            if (label.equalsIgnoreCase("uhcboard")) {
                if (args[0].equalsIgnoreCase("settimer")) {
                    if (args.length >= 4) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("s") && isInt(args[2])) {
                        CustomScoreboard.seconds = Integer.parseInt(args[2]);
                    } else if (args[1].equalsIgnoreCase("m") && isInt(args[2])) {
                        CustomScoreboard.minutes = Integer.parseInt(args[2]);
                    } else if (args[1].equalsIgnoreCase("h") && isInt(args[2])) {
                        CustomScoreboard.hours = Integer.parseInt(args[2]);
                    } else {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("limit")) {
                    if (args.length >= 4) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("s") && isInt(args[2])) {
                        this.plugin.config.set("Timer.Limit.seconds", Integer.parseInt(args[2]));
                        this.plugin.saveConfig();
                    } else if (args[1].equalsIgnoreCase("m") && isInt(args[2])) {
                        this.plugin.config.set("Timer.Limit.minutes", Integer.parseInt(args[2]));
                        this.plugin.saveConfig();
                    } else if (args[1].equalsIgnoreCase("h") && isInt(args[2])) {
                        this.plugin.config.set("Timer.Limit.hours", Integer.parseInt(args[2]));
                        this.plugin.saveConfig();
                    } else {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("setdisplay")) {
                    if (args.length > 2 || (!args[1].equals("hotbar") && !args[1].equals("onlytime") && !args[1].equals("waiting") && !args[1].equals("default"))) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    this.plugin.config.set("Display", args[1]);
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("settitle")) {
                    if (args.length > 2) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    sender.sendMessage(this.prefix + "Title has changed to " + ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' ')) + ". Reload the server to view it on the scoreboard");
                    this.plugin.config.set("Names.title", args[1].replace('_', ' '));
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("changeline")) {
                    if (args.length > 3) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("timer")) {
                        this.plugin.config.set("Names.timer", args[2]);
                    } else if (args[1].equalsIgnoreCase("team")) {
                        this.plugin.config.set("Names.team", args[2]);
                    } else if (args[1].equalsIgnoreCase("waiting")) {
                        this.plugin.config.set("Names.waiting", args[2]);
                    } else {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                    }
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("start")) {
                    if (args.length > 1) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    this.plugin.config.set("SaveData.start", true);
                    this.plugin.config.set("SaveData.stop", false);
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("stop")) {
                    if (args.length > 1) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    this.plugin.config.set("SaveData.stop", true);
                    this.plugin.config.set("SaveData.start", false);
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("countdown") && isBoolean(args[1])) {
                    if (args.length > 2) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    this.plugin.config.set("SaveData.countdown", args[1]);
                    this.plugin.saveConfig();
                    return true;
                }
                if (args[0].equalsIgnoreCase("show") && isBoolean(args[1])) {
                    if (args.length > 2) {
                        sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                        return true;
                    }
                    this.plugin.config.set("SaveData.show", Boolean.parseBoolean(args[1]));
                    this.plugin.saveConfig();
                    return true;
                }
                sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
                return true;
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(this.prefix + ChatColor.RED + "Bad usage. Use /help uhcboard for view the allowed commands");
            return true;
        }
    }

    public boolean isInt(String string) {
        try {
            int n = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isBoolean(String string) {
        return (string.equals("false") || string.equals("true"));
    }
}

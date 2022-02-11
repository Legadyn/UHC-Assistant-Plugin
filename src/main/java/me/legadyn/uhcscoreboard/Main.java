package me.legadyn.uhcscoreboard;

import me.legadyn.uhcscoreboard.teams.TeamEvents;
import me.legadyn.uhcscoreboard.teams.Teams;
import me.legadyn.uhcscoreboard.teams.TeamsCommandCompleter;
import me.legadyn.uhcscoreboard.teams.TeamsCommandExecutor;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

public final class Main extends JavaPlugin {
    FileConfiguration config = getConfig();

    ScoreboardCommandExecutor executor = new ScoreboardCommandExecutor(this);

    public void onEnable() {
        String rawv = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        int version = Integer.parseInt(rawv.split("_")[1]);
        ScoreboardLib.setPluginInstance((Plugin)this);
        addDefaults();
        this.config.options().copyDefaults(true);
        saveConfig();
        TeamsCommandExecutor teamExecutor = new TeamsCommandExecutor(this);
        Teams teams1 = new Teams(this);
        getCommand("uhcboard").setExecutor(this.executor);
        getCommand("uhcteams").setExecutor(teamExecutor);
        getCommand("uhcboard").setTabCompleter(new ScoreboardCommandCompleter());
        getCommand("uhcteams").setTabCompleter(new TeamsCommandCompleter());
        CustomScoreboard scoreboard = new CustomScoreboard(this, this.config.getInt("Timer.seconds"), this.config.getInt("Timer.minutes"), this.config.getInt("Timer.hours"), this.config);
        getServer().getPluginManager().registerEvents(scoreboard, this);
        getServer().getPluginManager().registerEvents(new TeamEvents(this), this);
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty())
            scoreboard.createScoreboard();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();
            Objective objective = board.getObjective("showhealth");
            if (objective == null) {
                String dName = ChatColor.RED + "\u2665";
                objective = board.registerNewObjective("showhealth", "health");
                objective.setDisplayName(dName);
                if (version >= 13) {
                    objective.setRenderType(RenderType.HEARTS);
                } else {
                    objective.getScore(p.getName()).setScore((int)p.getHealth());
                }
                objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }
        }
    }

    public void onLoad() {}

    public void onDisable() {
        this.config.set("Timer.hours", Integer.valueOf(CustomScoreboard.hours));
        this.config.set("Timer.minutes", Integer.valueOf(CustomScoreboard.minutes));
        this.config.set("Timer.seconds", Integer.valueOf(CustomScoreboard.seconds));
        saveConfig();
    }

    public void addDefaults() {
        this.config.addDefault("Display", "default");
        this.config.addDefault("Timer.hours", "03");
        this.config.addDefault("Timer.minutes", "30");
        this.config.addDefault("Timer.seconds", "00");
        this.config.addDefault("Timer.Limit.hours", Integer.valueOf(5));
        this.config.addDefault("Timer.Limit.minutes", Integer.valueOf(0));
        this.config.addDefault("Timer.Limit.seconds", Integer.valueOf(0));
        this.config.addDefault("Colors.title.primary", "&6&l");
        this.config.addDefault("Colors.title.glow", "&e&l");
        this.config.addDefault("Colors.hotbar.time", "&c&l");
        this.config.addDefault("Colors.hotbar.team", "&b&l");
        this.config.addDefault("Names.title", "UHC SCOREBOARD");
        this.config.addDefault("Names.timer", "&c&lTimer");
        this.config.addDefault("Names.team", "&b&lTeam");
        this.config.addDefault("Names.waiting", "&f&lWaiting");
        this.config.addDefault("SaveData.start", Boolean.valueOf(false));
        this.config.addDefault("SaveData.stop", Boolean.valueOf(true));
        this.config.addDefault("SaveData.countdown", Boolean.valueOf(true));
        this.config.addDefault("SaveData.show", Boolean.valueOf(true));
    }
}


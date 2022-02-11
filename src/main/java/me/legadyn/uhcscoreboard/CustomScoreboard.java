package me.legadyn.uhcscoreboard;

import java.text.DecimalFormat;
import java.util.List;
import me.legadyn.uhcscoreboard.teams.Teams;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.common.Strings;
import me.tigerhix.lib.scoreboard.common.animate.HighlightedString;
import me.tigerhix.lib.scoreboard.common.animate.ScrollableString;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;

public class CustomScoreboard implements Listener {
    public static Scoreboard scoreboard;

    Main main;

    Teams teams;

    static int seconds;

    static int minutes;

    static int hours;

    int version;

    FileConfiguration config;

    DecimalFormat df = new DecimalFormat("00");

    org.bukkit.scoreboard.Scoreboard scoreboardserver;

    public CustomScoreboard(Main main, int seconds, int minutes, int hours, FileConfiguration config) {
        this.main = main;
        CustomScoreboard.seconds = seconds;
        CustomScoreboard.minutes = minutes;
        CustomScoreboard.hours = hours;
        this.config = config;
        this.teams = new Teams(main);
        startTimer();
        String rawv = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        this.version = Integer.parseInt(rawv.split("_")[1]);
    }

    public void createScoreboard() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            scoreboard = ScoreboardLib.createScoreboard(player).setHandler(new ScoreboardHandler() {
                private final ScrollableString scroll = new ScrollableString(Strings.format("&aThis string is scrollable!"), 10, 0);

                private final HighlightedString highlighted = new HighlightedString(CustomScoreboard.this.config.getString("Names.title"), CustomScoreboard.this.config.getString("Colors.title.primary"), CustomScoreboard.this.config.getString("Colors.title.glow"));

                public String getTitle(Player player) {
                    return this.highlighted.next();
                }

                public List<Entry> getEntries(Player player) {
                    if (!CustomScoreboard.this.config.getBoolean("SaveData.show"))
                        return (new EntryBuilder()).build();
                    switch (CustomScoreboard.this.config.getString("Display")) {
                        case "hotbar":
                            CustomScoreboard.this.sendActionbar(player, ChatColor.GRAY + "   \u2022 " + CustomScoreboard.this.config.getString("Colors.hotbar.time") + CustomScoreboard.hours + ":" + CustomScoreboard.this.df.format(CustomScoreboard.minutes) + ":" + CustomScoreboard.this.df.format(CustomScoreboard.seconds) + " " + CustomScoreboard.this.config.getString("Colors.hotbar.team") + CustomScoreboard.this.teams.getTeamHealth(player, CustomScoreboard.this.teams.getTeam(player, true)));
                            return (new EntryBuilder()).build();
                        case "onlytime":
                            return (new EntryBuilder())
                                    .blank()
                                    .next(" " + CustomScoreboard.this.config.getString("Names.timer") + " ")
                                    .blank()
                                    .next(ChatColor.GRAY + "   \u2022 &f" + CustomScoreboard.hours + ":" + CustomScoreboard.this.df.format(CustomScoreboard.minutes) + ":" + CustomScoreboard.this.df.format(CustomScoreboard.seconds))
                                    .blank().build();
                        case "waiting":
                            return (new EntryBuilder())
                                    .blank()
                                    .next(" " + CustomScoreboard.this.config.getString("Names.waiting"))
                                    .blank().build();
                    }
                    return (new EntryBuilder())
                            .blank()
                            .next(" " + CustomScoreboard.this.config.getString("Names.timer") + " ")
                            .blank()
                            .next(ChatColor.GRAY + "   \u2022 &f" + CustomScoreboard.hours + ":" + CustomScoreboard.this.df.format(CustomScoreboard.minutes) + ":" + CustomScoreboard.this.df.format(CustomScoreboard.seconds))
                            .blank()
                            .next(" " + CustomScoreboard.this.config.getString("Names.team") + " ")
                            .blank()
                            .next(CustomScoreboard.this.teams.getTeamHealth(player, CustomScoreboard.this.teams.getTeam(player, true)))
                            .blank().build();
                }
            }).setUpdateInterval(10L);
            scoreboard.activate();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        createScoreboard();
        org.bukkit.scoreboard.Scoreboard board = e.getPlayer().getScoreboard();
        Objective objective = board.getObjective("showhealth");
        if (objective == null) {
            String dName = ChatColor.RED + "\u2665";
            objective = board.registerNewObjective("showhealth", "health");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            objective.setDisplayName(dName);
            if (this.version <= 13) {
                objective.getScore(e.getPlayer().getName()).setScore((int)e.getPlayer().getHealth());
                return;
            }
            objective.setRenderType(RenderType.HEARTS);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        scoreboard.deactivate();
    }

    private void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.main, new Runnable() {
            public void run() {
                if (CustomScoreboard.this.config.getBoolean("SaveData.stop"))
                    return;
                if (CustomScoreboard.this.config.getString("SaveData.countdown").equals("false")) {
                    CustomScoreboard.this.countup();
                } else {
                    CustomScoreboard.this.countdown();
                }
                CustomScoreboard.this.scoreTimer();
                CustomScoreboard.this.teams = new Teams(CustomScoreboard.this.main);
            }
        },  0L, 20L);
    }

    public void scoreTimer() {
        this.scoreboardserver = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = this.scoreboardserver.getObjective("hours");
        Objective objective1 = this.scoreboardserver.getObjective("minutes");
        Objective objective2 = this.scoreboardserver.getObjective("seconds");
        if (objective == null) {
            String dName = "hours";
            objective = this.scoreboardserver.registerNewObjective("hours", "dummy");
            objective.setDisplayName(dName);
        }
        if (objective1 == null) {
            String dName = "minutes";
            objective1 = this.scoreboardserver.registerNewObjective("minutes", "dummy");
            objective1.setDisplayName(dName);
        }
        if (objective2 == null) {
            String dName = "seconds";
            objective2 = this.scoreboardserver.registerNewObjective("seconds", "dummy");
            objective2.setDisplayName(dName);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.scoreboardserver.getObjective("hours").getScore(p.getName()).setScore(hours);
            this.scoreboardserver.getObjective("minutes").getScore(p.getName()).setScore(minutes);
            this.scoreboardserver.getObjective("seconds").getScore(p.getName()).setScore(seconds);
        }
    }

    public void countdown() {
        if (this.config.getString("Display").equals("waiting"))
            return;
        if (hours == 0 && seconds == 0 && minutes == 0)
            return;
        if (minutes == 0 && seconds == 0) {
            hours--;
            minutes = 59;
            seconds = 60;
        } else if (seconds == 0) {
            minutes--;
            seconds = 60;
        }
        seconds--;
    }

    public void countup() {
        if (this.config.getString("Display").equals("waiting"))
            return;
        if (hours == this.config.getInt("Timer.Limit.hours") && minutes == this.config.getInt("Timer.Limit.minutes") && seconds == this.config.getInt("Timer.Limit.seconds"))
            return;
        if (minutes == 59 && seconds == 59) {
            hours++;
            minutes = 0;
            seconds = -1;
        } else if (seconds == 59) {
            minutes++;
            seconds = -1;
        }
        seconds++;
    }

    public void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }
}

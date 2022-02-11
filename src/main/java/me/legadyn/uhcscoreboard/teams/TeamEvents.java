package me.legadyn.uhcscoreboard.teams;

import me.legadyn.uhcscoreboard.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamEvents implements Listener {
    Main plugin;

    Teams teams;

    public TeamEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        this.teams = new Teams(this.plugin);
        if (!this.teams.hasTeam(e.getPlayer()))
            return;
        e.setFormat(ChatColor.translateAlternateColorCodes('&', "&7[&c&lGLOBAL&7] &e" + e.getPlayer().getName() + " &f&l &r" + e.getMessage().replace("!", "")));
        if (!e.getMessage().contains("!")) {
            for (Player p : Bukkit.getOnlinePlayers())
                e.getRecipients().remove(p);
            for (Player p : this.teams.getPlayersByTeam(this.teams.getTeam(e.getPlayer(), true)))
                e.getRecipients().add(p);
            e.getRecipients().add(e.getPlayer());
            e.setFormat(ChatColor.translateAlternateColorCodes('&', "&7[&f" + this.teams.getTeam(e.getPlayer(), true) + "&7] &e" + e.getPlayer().getName() + " &f&l\u279B &r" + e.getMessage()));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        this.teams = new Teams(this.plugin);
        if (Boolean.parseBoolean(this.teams.data.getTeamsConfig().getString("AllowFriendly")))
            return;
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;
        if (!this.teams.hasTeam((Player)e.getEntity()))
            return;
        if (this.teams.getTeam((Player)e.getEntity(), true).equals(this.teams.getTeam((Player)e.getDamager(), true)))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.getEntity().setGameMode(GameMode.SPECTATOR);
    }
}

package me.legadyn.uhcscoreboard.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.legadyn.uhcscoreboard.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Teams {
    Main plugin;

    TeamsDataManager data;

    public HashMap<UUID, String> players = new HashMap<>();

    public List<String> teams;

    private List<String> playersData;

    private boolean allowFriendly;

    String prefix = ChatColor.translateAlternateColorCodes('&', "&f&l[&6&lUHC&f&l] &r");

    public Teams(Main plugin) {
        this.plugin = plugin;
        this.data = new TeamsDataManager(plugin);
        this.teams = this.data.getTeamsConfig().getStringList("Teams");
        updateHash();
        this.playersData = this.data.getTeamsConfig().getStringList("Players");
        this.allowFriendly = this.data.getTeamsConfig().getBoolean("AllowFriendly");
    }

    public void createTeam(String teamName, Player executor) {
        if (!this.teams.isEmpty())
            for (String t : this.teams) {
                if (colorFormat(t, true).equals(colorFormat(teamName, true)) || t.equals(teamName)) {
                    executor.sendMessage(ChatColor.RED + this.prefix + "You cannot create a team with this name, already exists");
                    return;
                }
            }
        this.teams.add(teamName);
        this.data.getTeamsConfig().set("Teams", this.teams);
        this.data.saveConfig();
        updateHash();
        executor.sendMessage(this.prefix + ChatColor.GREEN + "Succesfully created team " + ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.translateAlternateColorCodes('&', teamName) + ChatColor.WHITE + "" + ChatColor.BOLD + "]");
    }

    public void joinTeam(String teamName, Player executor) {
        if (!teamExists(teamName, executor))
            return;
        if (this.players.get(executor.getUniqueId()) != null) {
            executor.sendMessage(this.prefix + ChatColor.RED + "You are already on a team, leave it with /uhcteams leave");
            return;
        }
        for (String s : this.teams) {
            if (colorFormat(s, true).equals(teamName))
                this.playersData.add(executor.getUniqueId() + ":" + s);
        }
        this.data.getTeamsConfig().set("Players", this.playersData);
        this.data.saveConfig();
        updateHash();
        executor.setPlayerListName(ChatColor.translateAlternateColorCodes('&', getTeamColor(executor) + executor.getName()));
        executor.sendMessage(this.prefix + "You have joined to team " + getTeam(executor, false));
    }

    public void deleteTeam(String teamName, Player executor) {
        boolean remove = false;
        String toRemove = "";
        if (!teamExists(teamName, executor))
            return;
        for (String st : this.teams) {
            if (colorFormat(st, true).equals(teamName)) {
                remove = true;
                toRemove = st;
            }
        }
        if (remove) {
            cleanTeam(toRemove);
            this.teams.remove(toRemove);
            this.data.getTeamsConfig().set("Teams", this.teams);
            this.data.saveConfig();
            executor.sendMessage(this.prefix + "You have deleted team " + ChatColor.translateAlternateColorCodes('&', toRemove));
        }
    }

    public void deleteAll() {
        for (String s : this.teams)
            cleanTeam(s);
        this.teams.clear();
        this.data.getTeamsConfig().set("Teams", this.teams);
        this.data.saveConfig();
    }

    public void leaveTeam(Player executor) {
        if (this.players.get(executor.getUniqueId()) != null) {
            this.playersData.remove(executor.getUniqueId() + ":" + getTeam(executor, true));
            this.data.getTeamsConfig().set("Players", this.playersData);
            this.data.saveConfig();
            updateHash();
            executor.setPlayerListName(null);
            executor.sendMessage(this.prefix + "You have left the team");
        } else {
            executor.sendMessage(this.prefix + ChatColor.RED + "You cannot left the team because you're not in one");
        }
    }

    public void setAllowFriendly(String set) {
        this.data.getTeamsConfig().set("AllowFriendly", set);
        this.data.saveConfig();
    }

    public void cleanTeam(String teamName) {
        for (Player p : getPlayersByTeam(teamName)) {
            if (p != null)
                p.setPlayerListName(null);
        }
        this.playersData.removeIf(s -> s.contains(teamName));
        this.data.getTeamsConfig().set("Players", this.playersData);
        this.data.saveConfig();
        updateHash();
    }

    public String getTeam(Player executor, boolean raw) {
        if (this.players.get(executor.getUniqueId()) != null) {
            if (raw)
                return this.players.get(executor.getUniqueId());
            return ChatColor.translateAlternateColorCodes('&', this.players.get(executor.getUniqueId()));
        }
        return " ";
    }

    public ChatColor getTeamColor(Player executor) {
        if (!getTeam(executor, true).contains("&"))
            return ChatColor.WHITE;
        if (getTeam(executor, false).length() <= 1)
            return ChatColor.getByChar(getTeam(executor, false).charAt(0));
        return ChatColor.getByChar(getTeam(executor, false).charAt(1));
    }

    private void updateHash() {
        this.players.clear();
        for (String rawData : this.data.getTeamsConfig().getStringList("Players")) {
            String[] raw = rawData.split(":");
            this.players.put(UUID.fromString(raw[0]), raw[1]);
        }
    }

    public String colorFormat(String s, boolean format) {
        if (format) {
            String str = s;
            while (str.contains("&"))
                str = str.replace("&" + str.charAt(str.indexOf("&") + 1), "");
            return str;
        }
        return s;
    }

    public boolean teamExists(String teamName, Player executor) {
        boolean a = false;
        for (String s : this.teams) {
            if (!colorFormat(s, true).equals(teamName)) {
                a = false;
                continue;
            }
            a = true;
        }
        if (!a) {
            executor.sendMessage(ChatColor.RED + "Team " + ChatColor.translateAlternateColorCodes('&', teamName) + " doesn't exists ");
            return false;
        }
        return true;
    }

    public String getTeamHealth(Player player, String teamName) {
        if (getPlayersByTeam(teamName).isEmpty() || getPlayersByTeam(teamName).size() <= 1)
            return ChatColor.GRAY + "   \u2022 " + ChatColor.WHITE + " ";
        for (Player p : getPlayersByTeam(teamName)) {
            if (p == null)
                return ChatColor.GRAY + "   \u2022 " + ChatColor.WHITE + "No online";
            if (!p.equals(player)) {
                if (p.getGameMode() == GameMode.SPECTATOR)
                    return ChatColor.GRAY + "   \u2022 " + ChatColor.WHITE + "Muerto \u2620 ";
                int health = (int)Math.floor(p.getPlayer().getHealth() + p.getPlayer().getAbsorptionAmount());
                return ChatColor.GRAY + "   \u2022 " + ChatColor.WHITE + p.getPlayer().getName() + ": " + ChatColor.RED + health + ChatColor.WHITE + " \u2665";
            }
        }
        return " ";
    }

    public List<Player> getPlayersByTeam(String teamName) {
        List<Player> list = new ArrayList<>();
        for (String uuid : getKeysByValue(this.players, teamName))
            list.add(Bukkit.getPlayer(UUID.fromString(uuid)));
        return list;
    }

    public List<String> getPlayersNameByTeam(String teamName) {
        List<String> list = new ArrayList<>();
        for (String uuid : getKeysByValue(this.players, teamName))
            list.add(Bukkit.getPlayer(UUID.fromString(uuid)).getName());
        return list;
    }

    public boolean hasTeam(Player p) {
        return (this.players.get(p.getUniqueId()) != null);
    }

    public List<String> getKeysByValue(Map<UUID, String> map, String value) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue()))
                list.add(((UUID)entry.getKey()).toString());
        }
        return list;
    }
}

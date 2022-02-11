package me.legadyn.uhcscoreboard.teams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import me.legadyn.uhcscoreboard.Main;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TeamsDataManager {
    private Main plugin;

    private FileConfiguration config = null;

    private File file = null;

    public TeamsDataManager(Main plugin) {
        this.plugin = plugin;
        saveDefaults();
    }

    public void reloadConfig() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), "teams.yml");
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
        InputStream defaultConfig = this.plugin.getResource("teams.yml");
        if (defaultConfig != null) {
            YamlConfiguration configDefaults = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfig));
            this.config.setDefaults((Configuration)configDefaults);
        }
    }

    public FileConfiguration getTeamsConfig() {
        if (this.config == null)
            reloadConfig();
        return this.config;
    }

    public void saveConfig() {
        if (this.config == null || this.file == null)
            return;
        try {
            getTeamsConfig().save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaults() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), "teams.yml");
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!this.file.exists())
            this.plugin.saveResource(this.plugin.getDataFolder().getPath(), false);
    }
}


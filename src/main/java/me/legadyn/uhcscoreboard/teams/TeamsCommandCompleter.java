package me.legadyn.uhcscoreboard.teams;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TeamsCommandCompleter implements TabCompleter {
    List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            this.arguments.clear();
            results.clear();
            this.arguments.add("create");
            this.arguments.add("join");
            this.arguments.add("delete");
            this.arguments.add("deleteall");
            this.arguments.add("leave");
            this.arguments.add("leaveall");
            this.arguments.add("list");
            this.arguments.add("playerlist");
            this.arguments.add("allowfriendlyfire");
            for (String s : this.arguments) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    results.add(s);
            }
            return results;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("allowfriendlyfire")) {
                this.arguments.clear();
                results.clear();
                this.arguments.add("true");
                this.arguments.add("false");
            } else {
                this.arguments.clear();
                results.clear();
                this.arguments.add("");
            }
            for (String s : this.arguments) {
                if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                    results.add(s);
            }
            return results;
        }
        List<String> defaultResult = new ArrayList<>();
        defaultResult.add("");
        return defaultResult;
    }
}

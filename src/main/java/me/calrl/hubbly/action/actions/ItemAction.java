package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ItemAction implements Action {
    @Override
    public String getIdentifier() {
        return "ITEM";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        DebugMode debugMode = plugin.getDebugMode();
        String[] args = data.split(";");
        String item = args[0];
        if(args.length > 1 && args[1] != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hubbly:give " + player.getName() + " " + item + " 1 " +  Integer.parseInt(args[1]));
            debugMode.info(args[1]);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hubbly:give " + player.getName() + " " + item);
        }

    }
}

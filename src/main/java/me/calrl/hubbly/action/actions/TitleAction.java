package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleAction implements Action {
    @Override
    public String getIdentifier() {
        return "TITLE";
    }
    private DebugMode debugMode;

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        debugMode = plugin.getDebugMode();
        String title;
        String subtitle;
        int fadeIn;
        int stay;
        int fadeOut;
        try {
            String[] args = data.split(";");
            if(args.length != 5) {
                debugMode.warn("Invalid data format, expected 4 args, got " + args.length);
            }

            title = ChatUtils.translateHexColorCodes(args[0]);
            subtitle = ChatUtils.translateHexColorCodes(args[1]);
            fadeIn = Integer.parseInt(args[2]);
            stay = Integer.parseInt(args[3]);
            fadeOut = Integer.parseInt(args[4]);
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);

        } catch(NumberFormatException e) {
            debugMode.warn("Invalid data format, sending defaults");
            title = "Hubbly";
            subtitle = "";
            fadeIn = 20;
            stay = 200;
            fadeOut = 20;
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

    }
}

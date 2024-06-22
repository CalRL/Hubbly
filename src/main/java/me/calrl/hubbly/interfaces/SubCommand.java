package me.calrl.hubbly.interfaces;

import org.bukkit.entity.Player;

public interface SubCommand {
    void execute(Player player, String[] args);
}

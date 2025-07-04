package me.calrl.hubbly.hooks;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;

public interface HeadHook {
    ItemStack getHead(String data);
    HeadDatabaseAPI getApi();
}

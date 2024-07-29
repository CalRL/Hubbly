/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */

package me.calrl.hubbly.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChatUtils {
    public static String translateHexColorCodes(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        Pattern hexPattern = Pattern.compile( "<#[a-fA-F0-9]{6}>");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            String hexColor = matcher.group();
            String chatColor = ChatColor.of(hexColor.substring(1, hexColor.length()-1)).toString();
            matcher.appendReplacement(buffer, chatColor);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String parsePlaceholders(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        if(message.contains("%name%") && player != null) {
            message = message.replace("%name%", player.getName());
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }

    public static String repeat(String string, int count) {
        return new String(new char[count]).replace("\0", string);
    }

    public static String center(String string) {
        return string;
    }
}

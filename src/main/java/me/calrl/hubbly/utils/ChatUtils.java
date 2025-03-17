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

import me.calrl.hubbly.Hubbly;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private ChatUtils() {
        throw new IllegalStateException(ChatUtils.class + " is a Utility class");
    }
    public static String translateHexColorCodes(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        Pattern hexPattern = Pattern.compile( "<#[a-fA-F0-9]{6}>");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder builder = new StringBuilder();
        while(matcher.find()) {
            String hexColor = matcher.group();
            String chatColor = ChatColor.of(hexColor.substring(1, hexColor.length()-1)).toString();
            matcher.appendReplacement(builder, chatColor);
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    public static String centerMessage(String message) {
        message = translateHexColorCodes(message);
        message = translateCenterMessage(message);

        return message;
    }

    public static String processMessage(Player player, String message) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = translateHexColorCodes(message);
        return message;
    }

    public static String prefixMessage(Hubbly plugin, Player player, String message) {

        FileConfiguration config = plugin.getConfig();
        String prefix = config.getString("prefix");
        message = prefix + " " + message;

        message = processMessage(player, message);
        return message;
    }

    /**
     * This method is for console use, otherwise use the other one.
     * @param plugin
     * @param message
     * @return
     */
    public static String prefixMessage(Hubbly plugin, String message) {
        FileConfiguration config = plugin.getConfig();
        String prefix = config.getString("prefix");
        message = prefix + " " + message;

        message = translateHexColorCodes(message);
        return message;
    }

    private static String translateCenterMessage(String message) {
        Pattern centerPattern = Pattern.compile("<center>(.*?)</center>");
        Matcher matcher = centerPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String centeredText = matcher.group(1);
            String centeredMessage = center(centeredText);
            matcher.appendReplacement(buffer, centeredMessage);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String parsePlaceholders(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = translateHexColorCodes(message);

        if(message.contains("%name%")&& player != null) {
            message = message.replace("%name%", player.getName());
        } else if(message.contains("%player_name%") && player != null) {
            message = message.replace("%player_name%", player.getName());
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public static String repeat(String string, int count) {
        return new String(new char[count]).replace("\0", string);
    }

    public static String center(String message) {
        if(message == null || message.isEmpty()) {
            return "";
        }
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int centerPx = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = centerPx - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public static TextComponent textLinkBuilder(String message, String link, String hoverText) {
        TextComponent component = new TextComponent(message);
        String parsedHoverText = ChatUtils.translateHexColorCodes(hoverText);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(parsedHoverText)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        return component;
    }
}

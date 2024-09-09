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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String processMessage(String message) {
        message = translateHexColorCodes(message);
        message = translateCenterMessage(message);

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
        int CENTER_PX = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
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
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.translateHexColorCodes(hoverText)).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        return component;
    }
}

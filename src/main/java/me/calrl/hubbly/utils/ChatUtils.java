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

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChatUtils {
    public static String translateColorCodes(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        Pattern hexPattern = Pattern.compile( "#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            String hexColor = matcher.group();
            String chatColor = ChatColor.of(hexColor).toString();
            matcher.appendReplacement(buffer, chatColor);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}

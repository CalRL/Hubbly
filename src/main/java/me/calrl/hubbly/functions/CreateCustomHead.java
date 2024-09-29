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

package me.calrl.hubbly.functions;
import com.google.gson.JsonObject;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class CreateCustomHead {

    public static final ItemStack createCustomHead(String textureValue, String itemName) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if(skullMeta == null) { return null; }

        if (textureValue == null || textureValue.isEmpty()) {
            throw new IllegalArgumentException("Texture URL is null or empty.");
        }
        textureValue = textureValue.trim();


        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        if(textureValue.startsWith("http")) {
            try {
                URI uri = new URI(textureValue);
                URL url = uri.toURL();
                textures.setSkin(url);


            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);

            }
        } else {
            Hubbly.getInstance().getDebugMode().info("Texture value: " + textureValue + " is malformed. Please add 'https://' in front of the URL if not there already.");
            Hubbly.getInstance().getDebugMode().info("If that does not fix the issue, please get support in the discord");
            return head;
        }


        profile.setTextures(textures);

        skullMeta.setOwnerProfile(profile);
        skullMeta.setDisplayName(ChatUtils.translateHexColorCodes(itemName));

        head.setItemMeta(skullMeta);
        return head;
    }
}

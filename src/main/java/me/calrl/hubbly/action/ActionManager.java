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

package me.calrl.hubbly.action;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.actions.*;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ActionManager {
    private final Hubbly plugin;
    private final Map<String, Action> actions;

    public ActionManager(Hubbly plugin) {
        this.plugin = plugin;
        this.actions = new HashMap<>();
        load();
    }

    public void registerActions(Action... actions) {
        Arrays.asList(actions).forEach(action -> this.actions.put(action.getIdentifier(), action));
    }

    private void load() {
        registerActions(
                new PlayerCommandAction(),
                new ConsoleCommandAction(),
                new CloseAction(),
                new SoundAction(),
                new GamemodeAction(),
                new SoundAction(),
                new TitleAction(),
                new FireworkAction(),
                new BroadcastAction(),
                new ItemAction(),
                new BungeeAction()

        );
    }
    public void executeAction(Hubbly plugin, Player player, String actionData) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getLocation())) return;
        if (actionData.startsWith("[") && actionData.contains("]")) {
            int endIndex = actionData.indexOf("]");
            String identifier = actionData.substring(1, endIndex).toUpperCase();
            String data = actionData.substring(endIndex + 1).trim();

            Action action = actions.get(identifier);
            if (action != null) {
                action.execute(plugin, player, data);
            }
        }
    }

}

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
import me.calrl.hubbly.events.ActionEvent;
import me.calrl.hubbly.managers.DisabledWorlds;
import org.bukkit.entity.Player;

import java.util.*;

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
                new BungeeAction(),
                new MessageAction(),
                new LaunchAction(),
                new SlotAction(),
                new ClearAction()

        );
    }
    public void executeAction(Player player, String actionData) {
        DisabledWorlds disabledWorldsManager = plugin.getDisabledWorldsManager();
        boolean inDisabledWorld = disabledWorldsManager.inDisabledWorld(player.getLocation());

        if(inDisabledWorld) return;

        if (actionData.startsWith("[") && actionData.contains("]")) {
            String identifier = this.getIdentifier(actionData);
            String data = this.getData(actionData);

            Action action = actions.get(identifier);
            ActionEvent event = new ActionEvent(player, action, data);

            if (action == null) {
                String errorMessage = String.format("Action %s not found...", identifier);
                plugin.getLogger().warning(errorMessage);
                return;
            }

            plugin.getDebugMode().info("Checking if ActionEvent is cancelled...");
            if(event.isCancelled()) {
                return;
            }

            plugin.getDebugMode().info("Executing action: " + identifier);
            action.execute(plugin, player, data);
        }
    }

    public void executeActions(Player player, String actionData) {
        List<String> actionsData = Arrays.asList(actionData.split(","));
        this.executeActions(player, actionsData);
    }
    public void executeActions(Player player, List<String> actionsData) {
        List<String> identifiersList = this.getIdentifiers(actionsData);
        plugin.getDebugMode().info("Executing actions: " + String.join(",", identifiersList));
        for(String action : actionsData) {
            executeAction(player, action);
        }
    }

    public List<String> getIdentifiers(List<String> actionsData) {
        List<String> identifiers = new ArrayList<>();
        for(String action : actionsData) {
            identifiers.add(this.getIdentifier(action));

        }
        return identifiers;
    }

    public String getIdentifier(String actionData) {
        int endIndex = actionData.indexOf("]");
        return actionData.substring(1, endIndex).toUpperCase();
    }
    public String getData(String actionData) {
        int endIndex = actionData.indexOf("]");
        return actionData.substring(endIndex + 1).trim();
    }



}

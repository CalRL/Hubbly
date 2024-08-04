# Hubbly 2.0 Update

## IMPORTANT
- Socials item will be deprecated soon!
	- This means that socials will be gone soon in config but added in items.yml (read WIP)
## Actions
- -
- Player:
	- [PLAYER] {command}
	- Executes a command from the player's perspective
- Console:
	- [CONSOLE] {command}
	- Executes a command from the consoles perspective
- Bungee:
	- [BUNGEE] {command}
	- Executes a BungeeCord command from the player's perspective (Works with waterfall, velocity)
- Close:
	- [CLOSE]
	- No arguments, closes the player's open inventory (or gui's in general)
- Gamemode:
	- [GAMEMODE] {gamemode}
	- Changes a player's gamemode
- Message:
	- [MESSAGE] {message}
	- Sends a message to the player (supports hex color codes)
- Sound:
	- [SOUND] {sound}
	- Where {sound} is a minecraft sound
- Title:
	- [TITLE] title;subtitle;fadeIn;stay;fadeOut
	- fadeIn, stay, fadeOut in ticks
- Firework:
	- [FIREWORK] SHAPE;R;G;B;POWER
	- R, G, B being red blue green values
- Broadcast:
	- [BROADCAST] {message}
	- Broadcasts a message to all players
- Item:
	- [ITEM] ITEM;SLOT
	- Give a custom item defined in items.yml
	- Can leave SLOT empty (will just give to first available slot)
## General changes
- API version changed from 1.21 to 1.20.6, will updated back to 1.21 down the line when necessary
- Config.yml has changed so much its honestly more worth to just delete the Hubbly folder and let it recreate itself
- Launchpad power now configurable
- Player visibility and compass among other events don't work in disabled worlds
- Debug mode added!
- Flight QoL change, keeps state on logout
- Added hex color support
	- To Join and Leave messages
	- To the BossBar
	- To titles (using actions!)
	- Format: <#HEXCODE>
- `spawn_on_join` removed, replaced with `actions_on_join` [PLAYER] spawn.
- Removed SHOP item, now make your own custom item, by fault, a shop item will come in items.yml anyway.
- Added CommandBlocker:
	- Block commands in `blocked_commands` in config.yml.
	- Edit message in `message.blocked_command`
	- Permission `hubbly.bypass` bypasses command blocker
- Added ClearChat:
	- permission: `hubbly.command.clearchat`
- Fixed `/fly`, disabling flight now also stops flight if the player is flying.
- Added Blocked words:
	- `blocked_words` in config.yml
- Optimized the way custom items work
- `/hubbly selector` opens the selector.

# WIP 
#### (In order of priority)

- `/hubbly command` overhaul (to allow for console, tab complete, etc.)
- Menu Builder (and [MENU] action to open menus :) )
- Interval based announcements
- PvP mode
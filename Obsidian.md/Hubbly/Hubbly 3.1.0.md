- Added more action debugging
- ItemBuilder no longer checks for hdb if the section name isnt "hdb"
- Added extra debugging messages to DisabledWorlds
- Firework Action is now delayed by 1tick to fix issues with it being at the old location of the player when they join
- The trident now gets tracked when thrown, and returned when it lands
- Enderbow is now unbreakable
- Added command: /hubbly world
	- Add to disabled worlds list: /hubbly world add
	- Check if hubbly is enabled in a world: /hubbly world check
	- Remove from disabled worlds list: /hubbly world remove 
	Note: Will not save to config, only to memory. If you want the capability to save to config, please add it in #suggestions

Permissions
hubbly.commands.worlds
hubbly.commands.worlds.add
hubbly.commands.worlds.remove
hubbly.commands.worlds.check

Language file update

```
worlds:
    usage: /hubbly worlds <add | remove | check>
    description:
    add:
      usage: /hubbly worlds add <world>
      description:
      message: Added %world% to the disabled world list
    remove:
      usage: /hubbly worlds remove <world>
      description:
      message: Removed %world% from the disabled world list
    check:
      usage: /hubbly worlds check <world>
      description:
      enabled: Hubbly is enabled in world %world%
      disabled: Hubbly is disabled in world %world%
```
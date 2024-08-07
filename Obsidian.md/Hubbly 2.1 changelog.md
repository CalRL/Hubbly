# How permissions are changing in hubbly 2.1
Old -> new
hubbly.bypass.item (for drop/pickup):
- > hubbly.bypass.item.*
- > hubbly.bypass.item.pickup
- > hubbly.bypass.item.drop

hubbly.compass.use -> hubbly.use.compass
hubbly.playervisibility.use -> hubbly.use.playervisibility
hubbly.socials.use -> hubbly.use.socials
hubbly.shop.use -> removed

hubbly.clearchat -> hubbly.command.clearchat
hubbly.reload -> hubbly.command.reload
NEW IN 2.1 Version command -> hubbly.command.version

NEW IN 2.1 CommandBlocker -> hubbly.bypass.blockedcommands
NEW IN 2.1 AntiSwear -> hubbly.bypass.antiswear
hubbly.forceinventory.bypass -> hubbly.bypass.forceinventory

# General updates:

- `/clearchat` now clears the entirety of chat (used to be just visible chat)
- Permissions are now clearer and more in depth.
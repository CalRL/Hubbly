# CHANGELOG

## [3.2.0] - 06-07-2025
- Added trident manager, tridents will return to player if despawned.
- MessageBuilder now has a `nomessage` key that returns early.
- UpdateUtil will no longer send a message on join if the message is "nomessage" or " ".
- SpawnCommand is now a TabExecutor (todo: make /spawn %player%).
- Added `hubbly.command.debug` permission
- /spawn can now be configured to teleport after a few seconds
- Added `spawn.timer` in config
- Added `teleporting` and `teleport_cancelled` keys in en.yml
- Added `/hubbly worlds list` command
- OPs no longer always bypass `cancel_events` (you can deny them the permission)
- Fix `/hubbly worlds` `remove` and `check` subcommands
- `/hubbly worlds` `add` and `remove` now reflect in config
- LockChat now properly sends Locale message
- Added Portuguese language (Thank you snowy727. on discord)

## [3.3.0] - ??
- `launchpad.sound` key added, plays a sound when a launchpad is used.
- `spawn.sound` key added, plays a sound when /spawn teleport occurs.
- Fix `%new%` placeholder in update notifier on join
- added: cancel_events.animal_eat
- ItemFrames and the items they hold are no longer interactable/breakable.
- ArmorStands are no longer breakable and interactable
- Fix: if `spawn.timer` is 0, it will no longer check for movement (could not /spawn during freefall)
- DisabledWorlds no longer stops tracking if a world is reloaded
- Spawn timer no longer sends message if timer = 0
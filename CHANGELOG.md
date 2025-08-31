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
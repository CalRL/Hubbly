# CHANGELOG

## [3.2.0] - 06-07-2025
- Added `/hubbly debug disabledworlds` command with: `add`, `remove`, `check` subcommands.
- Added trident manager, tridents will return to player if despawned.
- MessageBuilder now has a "nomessage" key that returns early.
- UpdateUtil will no longer send a message on join if the message is "nomessage" or " ".
- SpawnCommand is now a TabExecutor (todo: make /spawn %player%).
- Added `hubbly.command.debug` permission
- /spawn can now be configured to teleport after a few seconds
- Added "spawn.timer" in config
- Added "teleporting" and "teleport_cancelled" keys in en.yml
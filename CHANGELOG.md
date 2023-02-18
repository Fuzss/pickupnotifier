# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.2.3-1.18.2] - 2023-02-18
### Added
- Added an option to include the total count of an item in your inventory in the pick-up entry
- Added an option to force a translucent background being drawn for entries (similar to the chat background, was available before, but only when the vanilla accessibility setting was set)
- Added an alternative background option to draw the item tooltip background
- Added an option to display the picked-up amount on the item sprite instead of as separate text just like in inventories
- Added an option to hide the picked-up amount when it's just a single item
### Removed
- Removed the option for specifying a max amount shown in a pick-up entry, long numbers are abbreviated now instead (e.g. 5000 turns to 5K)

## [v4.2.2-1.18.2] - 2023-02-17
### Added
- Added an option to disable new pick-ups being added to the log when the player is in creative mode

## [v4.2.1-1.18.2] - 2023-02-17
### Added
- Added dimension specific blacklists, also supports inverting the lists to be used as whitelists
- Check the `README` on [GitHub](https://github.com/Fuzss/pickupnotifier) for more information!

## [v4.2.0-1.19.2] - 2022-08-21
- Compiled for Minecraft 1.19.2
- Updated to Puzzles Lib v4.2.0

## [v4.1.0-1.19.1] - 2022-07-30
- Compiled for Minecraft 1.19.1
- Updated to Puzzles Lib v4.1.0

## [v4.0.0-1.19] - 2022-07-06
- Ported to Minecraft 1.19
- Split into multi-loader project

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/

# Changelog

## [neoforge/fabric-1.21.1-2.0.2]

### Added

- Config option to treat blacklist as whitelist.

## [1.1.3+forge/fabric1.20.1]

### Added

- Config option to treat blacklist as whitelist.

## [1.13+forge/fabric1.20.1]

### Added

- Config option to treat blacklist as whitelist.

## [1.1.2+fabric1.20.1]

### Added

- Backported from 2.0.1

## [2.0.1]

### Added

- Config to customize alert icon color
- Pausing with ESC reloads client config

## [1.1.2+forge1.20.1]

### Added

- Config to customize alert icon color

## [2.0.0-rc.1]

### Changed

- Move entire project to jaredlll08's multiloader architecture, should lower updating/maintenance cost in the future
- NeoForge's built-in config with a custom universal config that also works on Fabric, but no config screen integrations of any kind yet for both loaders

### Added

- As a side effect of switching to multiloader architecture, cross modloader compatibility, i.e. NeoForge client should work with Fabric server and vice versa

### Removed

- Server config

## [1.1.2+neoforge1.21]

### Fixed

- Crash due to java.lang.NoSuchMethodError from using old, non-existent method name from Neoforge beta in newer Neoforge versions

### Changed

- Bumped minimum Neoforge dependency to 21.0.167

## [1.1.1]

### Fixed

- Server crash on player log out

## [1.1.0+forge1.21]

### Changed

- Optimize memory-based target acquisition logic to use setter instead of on every tick

## [1.1.0-beta2+forge1.20.1]

### Changed

- Optimize memory-based target acquisition logic to use setter instead of on every tick

## [1.1.0-beta1+neoforge1.21]

### Added

- First port to Neoforge 1.21

## [1.1.0-beta1+forge1.20.1]

### Added

- Show aggro icon when goat is preparing to ram
- Add more aggro icons and the ability to switch between them with configs
- Clear client aggro list on world leave

### Fixed

- Fix aggro icon not properly reflecting Piglin's current behavior

### Changed

- Change the way the mod checks and compares targets similar to Fabric since standard Forge event hooks just aren't enough

## [1.0.1-beta1+fabric1.20.4]

### Added

- Show aggro icon when goat is preparing to ram
- Clear client aggro list on world leave

### Fixed

- Fix aggro icon not showing for Hoglin and Zoglin
- Fix aggro icon not properly reflecting Piglin's current behavior
- Fix not removing entry in client aggro list on mob death

### Changed

- Slightly lower y-offset for aggro icon

## [neoforge_1.20.4_1.0.0]

### Added

- Port to neoforge 1.20.4

## [fabric_1.20.4_1.0.0.0_beta1]

### Added

- Port to Fabric 1.20.4, no config for now

## [1.20.x-1.0.0.0]

### Changed

- Change the way the mod checks for mob rendering since RenderLivingEvent refused to fire when dealing with mobs from 
certain mods
- Use new alert icon

### Fixed

- Aggro not showing on mobs from several mods, including Creeper Overhaul, Enderman Overhaul, Naturalist and maybe more

## [1.20.x-0.0.0.6-beta1]

### Changed

- Raise priority of renderLivingEvent handler, implicit compat fix with Epic Fight mod

## [1.19.x-0.0.0.6-beta1]

### Changed

- Raise priority of renderLivingEvent handler, implicit compat fix with Epic Fight mod

## [1.20.x-0.0.0.5-beta1]

### Changed

- Ported to 1.20

## [1.19.2-0.0.0.5-beta1]

### Added

- More configs for scaling/positioning icons
- Blacklist on both server and client

## [1.19.2-0.0.0.4-beta1]

### Changed

- Require both server and client to have the mod installed. Installing on just the client or server won't crash but also won't do anything

### Fixed

- Crash on server startup

## [1.19.2-0.0.0.3-beta1]

### Changed

- Package name

### Fixed

- Config en/us translation

## [1.19.2-0.0.0.2-beta1]

### Added

- Configs

### Changed

- No longer renders alert if player is affected by blindness or darkness

## [1.19.2-0.0.0.1-beta1]

### Added

- Target syncing between server and client

## [1.19.2-0.0.0.0-beta1]

### Added

- Client renders alert icon

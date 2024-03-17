# Changelog

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

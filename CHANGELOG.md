# Changelog

All notable changes to TruHearts are documented here.

This file follows the [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format,
and the project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> Per-version source-of-truth changelogs live in
> [`.modrinth/changelogs/<version>.md`](.modrinth/changelogs/) — those are what
> Modrinth and GitHub Releases display for each release. This file aggregates them
> in reverse-chronological order for human browsing. When shipping a new release,
> add the version's file in `.modrinth/changelogs/` *and* prepend a new section here.

## [1.4.0] — 2026-08-03

Minecraft 26.3 compatibility.

- **Runs on the whole 26.3 line, snapshots included.** The declared range in
  `fabric.mod.json` moves from `>=26.1 <26.3-` to `>=26.1 <26.4-`. Because the
  loader normalizes MC snapshots into the semver prerelease slot
  (`26.3-snapshot-6` → `26.3-alpha.6`), that single range spans snapshot →
  pre → rc → release → patch. A jar built now keeps loading when 26.3 goes
  stable — no metadata change, no re-release.
- **Fixed a 26.3 build break.** `KeyBindings` took its unbound-key sentinel
  from `org.lwjgl.glfw.GLFW`, which is no longer on the compile classpath in
  26.3. It now uses `InputConstants.UNKNOWN.getValue()`, which resolves
  identically on every supported line. Behavior is unchanged.
- **Fabric API minimum is now declared** as `>=0.143.12` instead of `*`. An
  under-versioned install gets a clear loader message rather than a
  `NoSuchMethodError` at runtime. 0.143.12 is the first release carrying
  `HudElement.extractRenderState(GuiGraphicsExtractor, DeltaTracker)`, which
  every TruHearts overlay draws through — so the floor excludes only versions
  that genuinely cannot run the mod, and CI builds that exact pairing.
- **CI matrix widened** from two rows to four — 26.1 at the declared floor,
  26.1.2, 26.2, and a non-blocking 26.3 snapshot row. The 26.1 row closes a
  gap: that line was advertised as supported but never built. The 26.3 row is
  early warning for snapshot breakage and is allowed to fail without blocking
  a PR.
- **Toolchain:** Fabric Loom pinned to the `1.17.17` release rather than the
  moving `1.17-SNAPSHOT`; default Fabric API build target moved to
  `0.156.0+26.2`.

Listed Minecraft versions on Modrinth stay at 26.1–26.2 while 26.3 is in
snapshot. Nothing in the HP readout, damage log, or toggles changed.

## [1.3.1] — 2026-07-03

Damage-log label polish.

- Damage sources with camelCase or snake_case internal names now render
  cleanly out of the box: `WitherSkull` → **Wither skull**,
  `DragonBreath` → **Dragon breath**, `MaceSmash` → **Mace smash**,
  `BadRespawnPoint` → **Bad respawn point**, and any future vanilla
  source in the same shape.
- New explicit label for hot sulfur cube damage: **Magma sulfur cube**.
- Fixed: the label for axolotl / fish "drying out" damage now correctly
  renders as **Dry drowning** (was silently falling through to the
  default because the switch case was matching the wrong id-form).

## [1.3.0] — 2026-07-01

A new overlay: the last few hits you took.

- **Recent-damage log** stacks above the HP readout. Each entry shows the
  amount (as an un-rounded value like the HP readout) and a short label
  for the source — mob or player name for attacks, or the environmental
  cause (`Fall`, `Fire`, `Lava`, `Drowning`, `Explosion`, `Void`, …).
  Newest at the bottom; older entries stack upward and fade over the
  trailing ~2 s of a 10 s lifetime. Up to 5 entries on screen at once.
- **Death marker.** When the hit that killed you lands, a distinct
  `☠ Died to <source>` line drops into the stream right after the
  killing entry, rendered in a dim warm gray so it stands out from a
  regular hit.
- **Heal entries.** Recovery shows up as green `+ <amount>  <label>`
  (mirror of the damage red). Currently labeled `Regeneration` when
  the mob effect is active, `Healed` otherwise. Consecutive
  same-source heals within 2 s coalesce into one growing entry so
  tick-by-tick regen doesn't spam. Respawn HP snap-to-full is
  suppressed.
- Amount is measured as **health + absorption delta**, so hits eaten by
  absorption count at what the player actually felt.
- **Two-level toggle model.** The existing keybind is now the *master*
  switch (**Controls → TruHearts → Toggle everything (master)**) that
  hides everything TruHearts renders in one shot. A new keybind,
  **Toggle recent-damage log**, flips just the damage log while leaving
  the HP readout alone. Both are unbound by default; both preferences
  persist in `config/truhearts.json`.
- Coupled to vanilla-hearts visibility the same way the HP readout is:
  no vanilla hearts (creative, spectator) → no damage log.

Still one jar for MC 26.1 through 26.2.

## [1.2.1] — 2026-07-01

Two small fixes based on player feedback.

- The "TruHearts: on/off" toast now shows in **every gamemode**, not just
  survival. Previously the toast was silent in creative and spectator —
  the keybind fired and the state flipped, but you had no on-screen
  confirmation until you switched back to survival and noticed the HP
  overlay was gone. The toast now attaches to the same HUD slot as
  vanilla's held-item-name message so it renders wherever that does.
- Longer, more legible toast: **1.5 s total** (1 s hold + 0.5 s fade),
  up from 0.75 s.

The HP overlay itself is unchanged — still shown only when vanilla
hearts are visible (i.e., not in creative or spectator).

## [1.2.0] — 2026-07-01

Now runs on Minecraft 26.1 as well as 26.2 — one jar covers both.

- Adds support for Minecraft **26.1, 26.1.1, and 26.1.2**. The jar is
  source-compatible with both 26.1 and 26.2 lines.
- The "TruHearts: on/off" toggle message is now rendered by TruHearts
  directly (a brief 0.75 s linear fade), rather than calling vanilla's
  `setOverlayMessage`. That vanilla method's location differs between
  26.1 (`Gui`) and 26.2 (`Gui.hud`), so routing through our own element
  is what lets a single jar cover both.

Requires Fabric Loader 0.19.3+ and Fabric API matching your MC version.

## [1.1.0] — 2026-07-01

Toggle the HP readout on/off in-game.

- New keybind **Toggle HP overlay** under **Controls → TruHearts**. Unbound by
  default — assign it to whatever key you like.
- Preference persists across game sessions in `config/truhearts.json`.
- Action-bar message ("TruHearts: on/off") echoes the state change.

No changes to how the readout looks or where it sits when it's on.

## [1.0.0] — 2026-06-30

First stable release.

- Real (un-rounded) HP shown above the heart bar, with trailing-zero trim: `20`
  instead of `20.00`, `18.5` instead of `18.50`, `12.34` when precision warrants it.
- Absorption shown inline in gold (e.g. `+ 8`) when active, hidden when zero.
- Y-offset automatically clears the armor row and absorption-heart row so the
  readout never overlaps vanilla HUD elements.

Minecraft 26.2 with Fabric Loader 0.19.3+. Client-only — does nothing on a dedicated
server and isn't required there.

[1.4.0]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.4.0
[1.3.1]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.3.1
[1.3.0]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.3.0
[1.2.1]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.2.1
[1.2.0]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.2.0
[1.1.0]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.1.0
[1.0.0]: https://github.com/dbaggott/mc-truhearts/releases/tag/v1.0.0

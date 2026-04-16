# Zoomify Forge 1.7.10 Port

This project is a full Java rewrite of Zoomify `2.16.0+26.1` behavior for Forge `1.7.10`, written into this `rewrite` folder.

## Implemented

- Keybind zoom with `HOLD` and `TOGGLE` behaviors.
- Secondary zoom key with independent timing and zoom amount.
- Scroll zoom tiers with geometric scaling (`zoomPerStep`) and smooth stepping.
- Transition system parity:
  - Instant, Linear
  - Ease In/Out/InOut Sine
  - Ease In/Out/InOut Quad
  - Ease In/Out/InOut Cubic
  - Ease In/Out/InOut Exponential
- Relative sensitivity while zooming.
- Cinematic camera enabling while zooming/secondary zooming.
- Relative view bobbing legacy fallback.
- Secondary zoom HUD hide.
- First-launch key conflict notification.
- In-game config screen (`/zoomify` and Mod Options GUI entry).

## Legacy Mapping and 1.7.10 Assumptions

- `affectHandFov` in 1.7.10 uses a stack-trace heuristic to detect first-person hand render paths because Forge 1.7.10 does not expose separate world/hand FOV hooks.
- `relativeViewBobbing` uses a safe event-only fallback in 1.7.10: bobbing is disabled while zoomed, rather than continuously scaled, because exact scaling needs mixin/coremod-level camera hooks.
- Spyglass-related settings are retained for config parity, but vanilla 1.7.10 has no spyglass item, so these options are compatibility placeholders.

## Build

This project uses ForgeGradle `1.2` with Minecraft Forge `1.7.10-10.13.4.1614`.

If Gradle is installed globally:

```powershell
gradle -p . build
```

(Or open this folder as its own Gradle project and run `build`.)

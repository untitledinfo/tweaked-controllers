# Tweaked Controllers (Fabric port)

A Fabric **1.21.11** port of [**Create: Tweaked Controllers**](https://github.com/getItemFromBlock/Create-Tweaked-Controllers)
by getItemFromBlock (originally NeoForge-only), forked and ported by Firepdx.

- **Mod ID:** `ctc`
- **Package:** `com.firepdx.ctc`
- **Source branch ported:** `dev-1.21` (upstream, ~7,360 lines / 63 files)
- **Target:** Minecraft 1.21.11, Fabric Loader ≥0.18.1, Fabric API 0.140.2+1.21.11, Loom 1.14.10, Java 21
- **Hard dependency:** [Create Fly](https://modrinth.com/mod/create-fly) (community Fabric fork of Create)
- **Soft dependency:** CC: Tweaked (Fabric build), ModMenu

## Why 1.21.11 and not 1.21.1?

**There is no Fabric build of Create for Minecraft 1.21.1 — official or unofficial.**
This project originally targeted 1.21.1 (matching the NeoForge original), but Gradle
could never resolve a Create dependency for that version because none exists:

- Create's *official* Fabric fork ([Fabricators-of-Create/Create](https://github.com/Fabricators-of-Create/Create))
  stopped before 1.21.1 and has not been updated since.
- The actively maintained community fork, **[Create Fly](https://github.com/ZurrTum/Create-Fly)**,
  supports only 1.21.8, 1.21.10, and 1.21.11 (plus newer 26.1/26.2 snapshots) — nothing
  between there and 1.21.1.

So this project now targets **1.21.11**, where Create Fly has a current, actively
maintained build. If you need 1.21.1 specifically, your only real option today is
NeoForge, where the original mod already runs natively.

Yarn mappings are still published for 1.21.11 (`1.21.11+build.1`) — Fabric's last
version before switching to Mojang's official mappings — so this port stayed on Yarn
rather than also having to migrate every file's mapping names.

## Before you build this

This mod is a deep Create addon — it mixes into Create's `KineticBlockEntity` and
extends Create's block-entity/behaviour framework. A handful of integration points are
still best-effort and need verification against the real Create Fly jar:

1. **`gradle.properties`**'s `cc_tweaked_version` is a best guess — confirm the real
   1.21.11 version string at <https://modrinth.com/mod/cc-tweaked/versions> before building.
2. **Create Fly's mod ID** is assumed to be `create` (for drop-in compatibility with
   other Create addons) in `fabric.mod.json`'s `depends` block. If Create Fly registers
   under a different ID, update that line — check against its source at
   <https://github.com/ZurrTum/Create-Fly>.
3. **`ModComputerCraftProxy`** guesses at CC:Tweaked-Fabric's peripheral registration API
   (`dan200.computercraft.api.peripheral.PeripheralLookup`). Confirm the exact class
   against your CC:Tweaked-Fabric version.
4. **`CreateTweakedControllersClient`**'s item renderer registration assumes Create's
   `CustomRenderedItemModelRenderer` inherits `BlockEntityWithoutLevelRenderer#renderByItem`
   with the vanilla signature. Create Fly reimplements Create independently rather than
   forking it byte-for-byte, so double-check this against its actual class.
5. Everything under `com.simibubi.create.*` and `net.createmod.catnip.*` imports is
   assumed to exist with the same package/class names on Create Fly as on
   NeoForge-Create. Create Fly's own README states it re-registers content "in a way
   that's more consistent with vanilla Minecraft" rather than using Registrate — which
   actually matches the architectural choice already made in this port (see the table
   below) — but individual class package names may still differ. Spot-check imports if
   the build fails on a specific Create class.

Everything else — registries, networking, config, key bindings, the creative tab, block
entity/menu type registration, input handling, and around 40 files that were already
loader-agnostic — was rewritten or verified against Fabric API 0.140.2 / Yarn
`1.21.11+build.1` and should be solid.

## What changed structurally from the original NeoForge mod

| Concern | Original (NeoForge) | This port (Fabric) |
|---|---|---|
| Block/item/menu/BE-type registration | Create's `CreateRegistrate` | Plain Fabric/vanilla registries, via a small `RegistryEntries` shim (`.get()`/`.has()`/`.asStack()`/`.isIn()`) so most call sites didn't need touching |
| Networking | NeoForge `RegisterPayloadHandlersEvent` + `PacketDistributor` | Fabric Networking API v1 (`PayloadTypeRegistry`, `ServerPlayNetworking`, `ClientPlayNetworking`) — the `CustomPacketPayload` record classes themselves were already loader-agnostic and barely changed |
| Client config | NeoForge `ModConfigSpec` | Small Gson-backed JSON file (`ModClientConfig`), wrapped in a `ConfigValue<T>` shim with the same `.get()`/`.set()` call sites |
| Key bindings | NeoForge `RegisterKeyMappingsEvent` | Fabric API `KeyBindingHelper` |
| Item-handler ("ghost" frequency slots) | NeoForge `ItemStackHandler` | New `SimpleItemStackHandler` implementing vanilla `Container` directly |
| Controller menu | Create's NeoForge-only `GhostItemMenu` | Rewritten as a plain `AbstractContainerMenu` (`TweakedLinkedControllerMenu`) |
| Menu opening w/ extra data | `ServerPlayer#openMenu(this, buf -> ...)` | Fabric API `ExtendedScreenHandlerType` + `ExtendedScreenHandlerFactory<ItemStack>` |
| ComputerCraft peripheral registration | NeoForge `RegisterCapabilitiesEvent` | Best-effort CC:Tweaked `PeripheralLookup` call — **flagged for verification**, see above |
| "Any interaction key pressed" detection | NeoForge `InputEvent.InteractionKeyMappingTriggered` | Fabric API's four interaction callbacks (`AttackBlockCallback`, `AttackEntityCallback`, `UseBlockCallback`, `UseItemCallback`) combined |
| Config-button-on-title/pause-screen injection | NeoForge `ScreenEvent.Init.Post` | Two new Mixins, `TitleScreenMixin` / `PauseScreenMixin` |
| General settings screen | Create/Catnip's `SubMenuConfigScreen` reading a NeoForge `ModConfigSpec` | New hand-written `ModGeneralConfigScreen` (simple cycle-buttons) since the old screen was tied to `ModConfigSpec` |
| Common code referencing client-only classes | Not an issue (single source set) | Routed through a new `ClientBindings` indirection class, since Loom's split source sets don't allow common → client references |
| "Clear all slots" server sync | Create's `GhostItemMenu#sendClearPacket()` | New `TweakedLinkedControllerClearPacket` |
| `@OnlyIn(Dist.CLIENT)` annotations | Present throughout | Stripped; equivalent enforced by physically separating files into `src/client/java` |
| Create dependency | NeoForge Create (official) | [Create Fly](https://modrinth.com/mod/create-fly) — the only actively maintained Fabric build that covers a supported MC version |

## Project layout

```
src/main/java/com/firepdx/ctc/       # common code (both sides)
src/client/java/com/firepdx/ctc/     # client-only code (Loom split source set)
src/main/resources/
├── fabric.mod.json
├── ctc.mixins.json
├── assets/ctc/...                   # original textures/models/lang, carried over as-is
└── data/ctc/...                     # original recipes/loot tables/VS2 mass data
```

## Building

```bash
./gradlew build
```

## Credits

Original mod, design, textures, and models: **getItemFromBlock**
(https://github.com/getItemFromBlock/Create-Tweaked-Controllers). Create Fly (the Fabric
Create fork this depends on): **ZurrTum** (https://github.com/ZurrTum/Create-Fly). See
`CREDITS.txt` for the full original credits list. Fabric port: Firepdx.

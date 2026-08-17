# Tweaked Controllers (Fabric port)

A Fabric 1.21.1 port of [**Create: Tweaked Controllers**](https://github.com/getItemFromBlock/Create-Tweaked-Controllers)
by getItemFromBlock (originally NeoForge-only), forked and ported by Firepdx.

- **Mod ID:** `ctc`
- **Package:** `com.firepdx.ctc`
- **Source branch ported:** `dev-1.21` (upstream, ~7,360 lines / 63 files)
- **Target:** Minecraft 1.21.1, Fabric Loader ≥0.16.9, Fabric API 0.107.0+1.21.1, Java 21
- **Hard dependency:** Create (Fabric build)
- **Soft dependency:** CC: Tweaked (Fabric build), ModMenu

## Before you build this

This mod is a deep Create addon — it mixes into Create's `KineticBlockEntity`, extends
Create's block-entity/behaviour framework, and (originally) used Create's Registrate
helper. This port was written **without access to a real Create-Fabric jar to compile
against**, so a handful of integration points are best-effort and need verification:

1. **`build.gradle` dependency coordinates** for Create-Fabric and CC:Tweaked-Fabric are
   placeholders — find the real artifact/version from Create's Modrinth page or Fabric
   maven and swap them in.
2. **`ModComputerCraftProxy`** guesses at CC:Tweaked-Fabric's peripheral registration API
   (`dan200.computercraft.api.peripheral.PeripheralLookup`). Confirm the exact class
   against your CC:Tweaked-Fabric version.
3. **`CreateTweakedControllersClient`**'s item renderer registration assumes Create's
   `CustomRenderedItemModelRenderer` inherits `BlockEntityWithoutLevelRenderer#renderByItem`
   with the vanilla signature. If that doesn't compile, check what Create-Fabric's
   equivalent class actually extends.
4. Everything under `com.simibubi.create.*` and `net.createmod.catnip.*` imports is
   assumed to exist with the same package/class names on Create-Fabric as on
   NeoForge-Create, since Create's codebase is multiloader upstream. Spot-check imports
   if the build fails on a specific Create class.

Everything else — registries, networking, config, key bindings, the creative tab, block
entity/menu type registration, input handling, and around 40 files that were already
loader-agnostic — was rewritten or verified against Fabric API 0.107.0 / Yarn
`1.21.1+build.3` and should be solid.

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

(Run `gradle wrapper` first if `gradle/wrapper/gradle-wrapper.jar` isn't present — it
wasn't bundled here since this environment has no network access to
`services.gradle.org`.)

## Credits

Original mod, design, textures, and models: **getItemFromBlock**
(https://github.com/getItemFromBlock/Create-Tweaked-Controllers). See `CREDITS.txt` for
the full original credits list. Fabric port: Firepdx.

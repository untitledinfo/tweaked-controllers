# Tweaked Controllers

A Fabric mod for **Minecraft 1.21.1** that adds tweakable "Controller" blocks
along with an open API so other mods can register their own controller
behavior without needing to subclass anything.

- **Mod ID:** `ctc`
- **Package:** `com.firepdx.ctc`
- **Loader:** Fabric (Loom 1.9)
- **Target:** Minecraft 1.21.1 / Yarn `1.21.1+build.3` / Fabric API `0.107.0+1.21.1`
- **Java:** 21

## Project layout

```
src/main/java/com/firepdx/ctc/
├── TweakedControllers.java        # ModInitializer (common entrypoint)
├── api/
│   ├── TweakedControllersApi.java # Public API singleton (getItemFromBlock, behaviors)
│   └── ModSupportRegistry.java    # Lets other mods register compat bridges
├── block/
│   └── ControllerBlock.java       # Example controller block
└── registry/
    ├── ModBlocks.java
    └── ModItems.java

src/client/java/com/firepdx/ctc/client/
└── TweakedControllersClient.java  # ClientModInitializer

src/main/resources/
├── fabric.mod.json
├── ctc.mixins.json
├── assets/ctc/...                 # lang, blockstates, models
└── data/ctc/...                   # recipe + loot table
```

## Building

```bash
./gradlew build
```

The output jar is written to `build/libs/tweaked-controllers-1.0.0.jar`.
Drop it (plus **Fabric API**) into your `mods/` folder for a 1.21.1 Fabric client/server.

## Using the API from another mod

Add Tweaked Controllers as a `modCompileOnly`/`modRuntimeOnly` dependency, then:

```java
import com.firepdx.ctc.api.TweakedControllersApi;

TweakedControllersApi api = TweakedControllersApi.INSTANCE;

// Resolve a Block's Item form (never throws, returns AIR if none exists)
Item item = api.getItemFromBlock(MyBlocks.MY_CONTROLLER);

// Same thing, but distinguishing "no item" from "air block"
Optional<Item> maybeItem = api.getItemFromBlockOptional(MyBlocks.MY_CONTROLLER);

// Build a stack directly
ItemStack stack = api.getStackFromBlock(MyBlocks.MY_CONTROLLER);

// Look a block up by its registry id string (works across mods)
Item createPress = api.getItemFromBlockId("create:mechanical_press");

// Register custom behavior for a controller block (yours or ours)
api.registerBehavior(MyBlocks.MY_CONTROLLER, (world, pos, powered) -> {
    // react to the toggle
});

// Or listen globally to every controller toggle in the game
api.addGlobalToggleListener((world, pos, block, powered) -> {
    // ...
});
```

## Registering a full mod-support bridge

For larger compat modules, declare a custom entrypoint in **your** mod's
`fabric.mod.json`:

```json
{
  "entrypoints": {
    "ctc:mod_support": [
      "com.example.mymod.compat.CtcCompat"
    ]
  }
}
```

```java
package com.example.mymod.compat;

import com.firepdx.ctc.api.ModSupportRegistry;
import com.firepdx.ctc.api.TweakedControllersApi;

public class CtcCompat implements ModSupportRegistry.TweakedControllersModSupport {
    @Override
    public void onModSupport(TweakedControllersApi api) {
        api.registerBehavior(MyBlocks.MY_CONTROLLER, (world, pos, powered) -> {
            // ...
        });
        ModSupportRegistry.markSupported("mymod");
    }
}
```

This entrypoint only fires if the depending mod is actually installed —
Tweaked Controllers never hard-depends on any third-party mod.

## Notes

- Textures (`controller_block.png`, `controller_block_on.png`) are not
  included — drop 16x16 PNGs into `src/main/resources/assets/ctc/textures/block/`.
- `ctc.mixins.json` is registered but empty; add mixin classes under
  `com.firepdx.ctc.mixin` if you need to hook into vanilla/Fabric API internals.

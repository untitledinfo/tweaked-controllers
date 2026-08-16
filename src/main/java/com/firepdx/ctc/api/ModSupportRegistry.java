package com.firepdx.ctc.api;

import com.firepdx.ctc.TweakedControllers;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which third-party mods have registered compatibility with Tweaked Controllers,
 * so compat modules (e.g. a Create.jar bridge) only load when the target mod is present.
 * <p>
 * Usage from a compat entrypoint (registered under the {@code "ctc:mod_support"}
 * custom entrypoint key in a dependent mod's fabric.mod.json):
 * <pre>{@code
 * public class CreateCompat implements ModSupportRegistry.TweakedControllersModSupport {
 *     public void onModSupport(TweakedControllersApi api) {
 *         api.registerBehavior(SomeCreateBlock.INSTANCE, (world, pos, powered) -> { ... });
 *         ModSupportRegistry.markSupported("create");
 *     }
 * }
 * }</pre>
 */
public final class ModSupportRegistry {

    private static final Set<String> SUPPORTED_MOD_IDS = ConcurrentHashMap.newKeySet();
    public static final String ENTRYPOINT_KEY = "ctc:mod_support";

    private ModSupportRegistry() {
    }

    public static void markSupported(String modId) {
        if (SUPPORTED_MOD_IDS.add(modId)) {
            TweakedControllers.LOGGER.info("[{}] Loaded mod support bridge for '{}'", TweakedControllers.MOD_ID, modId);
        }
    }

    public static boolean isSupported(String modId) {
        return SUPPORTED_MOD_IDS.contains(modId);
    }

    public static Set<String> getSupportedModIds() {
        return Set.copyOf(SUPPORTED_MOD_IDS);
    }

    /**
     * Runs every {@link TweakedControllersModSupport} entrypoint declared by other mods
     * under {@value #ENTRYPOINT_KEY}. Call once during mod init, after your own blocks
     * and the {@link TweakedControllersApi} are ready.
     */
    public static void dispatchAll() {
        net.fabricmc.loader.api.FabricLoader.getInstance()
                .getEntrypoints(ENTRYPOINT_KEY, TweakedControllersModSupport.class)
                .forEach(entry -> {
                    try {
                        entry.onModSupport(TweakedControllersApi.INSTANCE);
                    } catch (Throwable t) {
                        TweakedControllers.LOGGER.error("[{}] A mod support bridge threw during init", TweakedControllers.MOD_ID, t);
                    }
                });
    }

    /** Implemented by other mods to hook into Tweaked Controllers at startup. */
    @FunctionalInterface
    public interface TweakedControllersModSupport {
        void onModSupport(TweakedControllersApi api);
    }
}

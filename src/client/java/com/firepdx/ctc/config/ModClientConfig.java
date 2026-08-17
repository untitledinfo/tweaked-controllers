package com.firepdx.ctc.config;

import com.firepdx.ctc.CreateTweakedControllers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Replaces NeoForge's ModConfigSpec with a plain Gson-backed JSON file under
 * .minecraft/config/ctc-client.json. Values are exposed the same way call sites
 * previously used them (a small ConfigValue<T> wrapper providing .get()/.set()) so the
 * rest of the mod barely changed.
 */
public class ModClientConfig
{
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(CreateTweakedControllers.ID + "-client.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final ConfigValue<Boolean> USE_CUSTOM_MAPPINGS = new ConfigValue<>("use_custom_mappings", false);
    public static final ConfigValue<Boolean> TOGGLE_MOUSE_FOCUS = new ConfigValue<>("toggle_mouse_focus", false);
    public static final ConfigValue<Boolean> AUTO_RESET_MOUSE_FOCUS = new ConfigValue<>("auto_reset_mouse_focus", true);
    public static final ConfigValue<Integer> CONFIG_BUTTON_MAIN_MENU_ROW = new ConfigValue<>("config_button_main_menu_row", 2);
    public static final ConfigValue<Integer> CONFIG_BUTTON_MAIN_MENU_OFFSET = new ConfigValue<>("config_button_main_menu_offset", 4);
    public static final ConfigValue<Integer> CONFIG_BUTTON_INGAME_MENU_ROW = new ConfigValue<>("config_button_ingame_menu_row", 3);
    public static final ConfigValue<Integer> CONFIG_BUTTON_INGAME_MENU_OFFSET = new ConfigValue<>("config_button_main_ingame_offset", 4);
    public static final ConfigValue<ControllerLayoutType> CONTROLLER_LAYOUT_TYPE = new ConfigValue<>("controller_layout_type", ControllerLayoutType.XBOX);

    public enum ControllerLayoutType
    {
        XBOX,
        NINTENDO,
        PLAYSTATION
    }

    private static Data data = new Data();

    public static void load()
    {
        if (Files.exists(PATH))
        {
            try (var reader = Files.newBufferedReader(PATH))
            {
                Data loaded = GSON.fromJson(reader, Data.class);
                if (loaded != null)
                    data = loaded;
            }
            catch (IOException e)
            {
                CreateTweakedControllers.error("Failed to load client config: " + e.getMessage());
            }
        }
        save();
    }

    public static void save()
    {
        try
        {
            Files.createDirectories(PATH.getParent());
            try (var writer = Files.newBufferedWriter(PATH))
            {
                GSON.toJson(data, writer);
            }
        }
        catch (IOException e)
        {
            CreateTweakedControllers.error("Failed to save client config: " + e.getMessage());
        }
    }

    /** Plain data holder that gets (de)serialized directly by Gson. */
    private static final class Data
    {
        boolean use_custom_mappings = false;
        boolean toggle_mouse_focus = false;
        boolean auto_reset_mouse_focus = true;
        int config_button_main_menu_row = 2;
        int config_button_main_menu_offset = 4;
        int config_button_ingame_menu_row = 3;
        int config_button_main_ingame_offset = 4;
        String controller_layout_type = ControllerLayoutType.XBOX.name();
    }

    /** Mimics NeoForge's ModConfigSpec.ConfigValue<T> surface (get()/set()) used across the mod. */
    public static final class ConfigValue<T>
    {
        private final String key;
        private final T defaultValue;

        private ConfigValue(String key, T defaultValue)
        {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @SuppressWarnings("unchecked")
        public T get()
        {
            try
            {
                java.lang.reflect.Field field = Data.class.getDeclaredField(key);
                Object value = field.get(data);
                if (defaultValue instanceof ControllerLayoutType)
                    return (T) ControllerLayoutType.valueOf((String) value);
                return (T) value;
            }
            catch (ReflectiveOperationException e)
            {
                return defaultValue;
            }
        }

        public void set(T value)
        {
            try
            {
                java.lang.reflect.Field field = Data.class.getDeclaredField(key);
                field.set(data, value instanceof Enum<?> e ? e.name() : value);
                save();
            }
            catch (ReflectiveOperationException e)
            {
                CreateTweakedControllers.error("Failed to set config value " + key + ": " + e.getMessage());
            }
        }
    }
}

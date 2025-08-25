package com.neins.smava;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SmavaConfig {

    // Config values with defaults
    public static float radius_water = 2.0f;
    public static float radius_moss = 2.0f;
    public static int speed_water = 300;
    public static int speed_moss = 300;
    public static int speed_rain = 300;

    private static final String CONFIG_FILE_NAME = "smava.properties";

    public static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        Properties props = new Properties();

        try {
            if (Files.exists(configPath)) {
                props.load(Files.newInputStream(configPath));

                // Parse and validate float values
                radius_water = parseFloat(props.getProperty("radius_water"), radius_water, 0f, Float.MAX_VALUE);
                radius_moss = parseFloat(props.getProperty("radius_moss"), radius_moss, 0f, Float.MAX_VALUE);

                // Parse and validate integer values
                speed_water = parseInt(props.getProperty("speed_water"), speed_water, 0, 6000);
                speed_moss = parseInt(props.getProperty("speed_moss"), speed_moss, 0, 6000);
                speed_water = parseInt(props.getProperty("speed_water"), speed_water, 0, 6000);

            } else {
                saveDefaultConfig(configPath, props);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDefaultConfig(Path configPath, Properties props) {
        try {
            props.setProperty("radius_water", String.valueOf(radius_water));
            props.setProperty("radius_moss", String.valueOf(radius_moss));
            props.setProperty("speed_water", String.valueOf(speed_water));
            props.setProperty("speed_moss", String.valueOf(speed_moss));
            props.setProperty("speed_rain", String.valueOf(speed_rain));

            Files.createDirectories(configPath.getParent());
            props.store(Files.newOutputStream(configPath), " Smava Config \n Disable = 0 \nAccepts values from 0 to 6000 where a higher number means a faster speed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        Properties props = new Properties();
        props.setProperty("radius_water", String.valueOf(radius_water));
        props.setProperty("radius_moss", String.valueOf(radius_moss));
        props.setProperty("speed_water", String.valueOf(speed_water));
        props.setProperty("speed_moss", String.valueOf(speed_moss));
        props.setProperty("speed_rain", String.valueOf(speed_rain));

        try {
            props.store(Files.newOutputStream(configPath), "Smava Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float parseFloat(String value, float fallback, float min, float max) {
        try {
            float f = Float.parseFloat(value);
            if (f < min) return min;
            if (f > max) return max;
            return f;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int parseInt(String value, int fallback, int min, int max) {
        try {
            int i = Integer.parseInt(value);
            if (i < min) return min;
            if (i > max) return max;
            return i;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}

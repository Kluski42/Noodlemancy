package net.wetnoodle.noodlemancy.config;

import com.mojang.datafixers.util.Pair;
import net.wetnoodle.noodlemancy.NMConstants;

public class NMConfig {
    public static SimpleConfig CONFIG;
    private static NMConfigProvider configs;

    public static boolean SNEEZER_ENABLED;
    public static boolean CREAKING_EYE_ENABLED;

    public static void registerConfigs() {
        configs = new NMConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(NMConstants.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("sneezerEnabled", true), "enables sneezers.");
        configs.addKeyValuePair(new Pair<>("creakingEyeEnabled", true), "enables creaking eyes.");
    }

    private static void assignConfigs() {
        SNEEZER_ENABLED = CONFIG.getOrDefault("sneezerEnabled", true);
        CREAKING_EYE_ENABLED = CONFIG.getOrDefault("creakingEyeEnabled", true);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}

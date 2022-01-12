package com.zener.origin_orbs;

import java.io.File;
import java.util.ArrayList;

import com.oroarmor.config.ArrayConfigItem;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;

import net.fabricmc.loader.api.FabricLoader;

public class OrbsConfig extends Config {


    public OrbsConfig() {
        super(new ArrayList<ConfigItemGroup>(){{ add(main); }}, new File(FabricLoader.getInstance().getConfigDir().toFile(), "orbs_config.json"), "orbs_config");
    }


    public static final ConfigItemGroup main = new TopNest();

    public static class TopNest extends ConfigItemGroup {
        public static final String[] orb_array = (new ArrayList<String>() {{add("orb"); for (int i = 0; i < 255; i++) { add(""); } }}).toArray(new String[256]);
        public static final ArrayConfigItem<String> orbs = new ArrayConfigItem<>("orbs", orb_array, "orbs");

        public TopNest() {
            super(new ArrayList<ConfigItem<?>>(){{ add(orbs); }}, "orbs_config");
        }

    }

    
}
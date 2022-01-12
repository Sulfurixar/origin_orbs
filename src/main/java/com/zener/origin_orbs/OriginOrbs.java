package com.zener.origin_orbs;

import com.oroarmor.config.ArrayConfigItem;
import com.oroarmor.config.Config;
import com.zener.origin_orbs.OrbsConfig.TopNest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OriginOrbs implements ModInitializer {

    public static final String MOD_ID = "origin_orbs";

    public static Config CONFIG = new OrbsConfig();

    @Override
    public void onInitialize() {

        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();

        CONFIG.getConfigs().forEach((configGroup) -> {
            if (configGroup.getName() == "orbs_config" && configGroup instanceof TopNest) {
                configGroup.getConfigs().forEach((config -> {
                    if (config.getName() == "orbs" && config instanceof ArrayConfigItem) {
                        Object[] arr = ((ArrayConfigItem<?>) config).getValue();
                        if (arr.length > 0 && arr[0] instanceof String) {
                            for (int i = 0; i < arr.length; i++) {
                                String orb_name = (String) arr[i];
                                if (orb_name != null && !orb_name.equalsIgnoreCase("")) {
                                    Registry.register(Registry.ITEM, new Identifier(MOD_ID, orb_name), new Item(new Item.Settings().group(ItemGroup.MISC)));
                                }
                            }
                        }
                    }
                }));
                
            }
        });
    }

    
    
}

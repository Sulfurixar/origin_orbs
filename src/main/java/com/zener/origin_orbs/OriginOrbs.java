package com.zener.origin_orbs;

import java.util.HashMap;
import java.util.Map;

import com.oroarmor.config.ArrayConfigItem;
import com.oroarmor.config.Config;
import com.zener.origin_orbs.OrbsConfig.TopNest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OriginOrbs implements ModInitializer {

    public static final String MOD_ID = "origin_orbs";

    private static final Logger LOGGER = LogManager.getLogger();

    public static Config CONFIG = new OrbsConfig();

    public static final Identifier PRESENCE_CHANNEL = new Identifier(MOD_ID, "present");
    
    public static final Identifier UPDATE_ORIGIN_ORBS_PACKET_ID = new Identifier(MOD_ID, "update_origin_orbs");


    private static final Map<Item, Identifier> ITEMS = new HashMap<Item, Identifier>();

    public static ThreadLocal<ServerPlayerEntity> lastServerPlayerEntity = new ThreadLocal<>();

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
                                    Identifier id = new Identifier(MOD_ID, orb_name);
                                    Item item = new Item(new Item.Settings().group(ItemGroup.MISC));
                                    Registry.register(Registry.ITEM, id, item);
                                    ITEMS.put(item, id);
                                }
                            }
                        }
                    }
                }));
                
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			OriginOrbs.logInfo("Syncing origin_orbs data to player2.");
			PacketByteBuf buf = OriginOrbs.createAdvancedRecipeSyncPacket();
            sender.sendPacket(OriginOrbs.UPDATE_ORIGIN_ORBS_PACKET_ID, buf);
        });

    }


    public static PacketByteBuf createAdvancedRecipeSyncPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(ITEMS.size());
        ITEMS.forEach((item, id) -> {
            buf.writeItemStack(new ItemStack(item));
            buf.writeIdentifier(id);
        });
        
		return buf;
	}

    public static void logInfo(String message) {
		LOGGER.info(message);
	}

    
    
}

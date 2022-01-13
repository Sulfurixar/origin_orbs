package com.zener.origin_orbs;

import java.util.HashMap;
import java.util.Map;

import com.oroarmor.config.ArrayConfigItem;
import com.oroarmor.config.Config;
import com.zener.origin_orbs.OrbsConfig.TopNest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
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

    private static final IntSet hasModClientConnectionHashes = IntSets.synchronize(new IntAVLTreeSet());

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

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			sender.sendPacket(PRESENCE_CHANNEL, new PacketByteBuf(Unpooled.buffer()));
		});

        ServerLoginConnectionEvents.DISCONNECT.register((handler, server) -> {
			hasModClientConnectionHashes.remove(handler.getConnection().hashCode());
		});

        ServerLoginNetworking.registerGlobalReceiver(PRESENCE_CHANNEL, (server, handler, understood, buf, synchronizer, responseSender) -> {
			if (understood) {
				hasModClientConnectionHashes.add(handler.getConnection().hashCode());
			}
		});

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (hasModClientConnectionHashes.contains(handler.getConnection().hashCode())) {
				((IServerPlayerEntity) handler.player).origin_orbs$setClientModPresent(true);
				hasModClientConnectionHashes.remove(handler.getConnection().hashCode());
			}
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			OriginOrbs.logInfo("Syncing origin_orbs data to player.");
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

    public static boolean hasClientMod(ServerPlayerEntity playerEntity) {
		if (playerEntity instanceof IServerPlayerEntity) {
			return ((IServerPlayerEntity) playerEntity).origin_orbs$hasClientMod();
		}
		return false;
	}

    public static void logInfo(String message) {
		LOGGER.info(message);
	}

    
    
}

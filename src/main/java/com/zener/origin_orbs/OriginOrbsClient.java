package com.zener.origin_orbs;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OriginOrbsClient implements ClientModInitializer {
    
	@Override
	public void onInitializeClient() {

        ClientPlayConnectionEvents.JOIN.addPhaseOrdering(OriginOrbs.UPDATE_ORIGIN_ORBS_PACKET_ID, new Identifier("fabric", "registry/sync"));

        ClientPlayNetworking.registerGlobalReceiver(OriginOrbs.UPDATE_ORIGIN_ORBS_PACKET_ID, (client, handler, buf, responseSender) -> {
            OriginOrbs.logInfo("Listening to origin_orbs_update2.");
			int orb_count = buf.readInt();
            for (int i = 0; i < orb_count; i++) {
                buf.readItemStack();
                Identifier id = buf.readIdentifier();
                if (!Registry.ITEM.containsId(id)) {
                    OriginOrbs.logInfo("Added " + id.getPath());
                    Registry.register(Registry.ITEM, id, new Item(new Item.Settings().group(ItemGroup.MISC)));
                }
            }
		});

	}
}
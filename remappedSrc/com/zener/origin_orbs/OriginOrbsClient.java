package com.zener.origin_orbs;

import java.util.concurrent.CompletableFuture;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OriginOrbsClient implements ClientModInitializer {
    
	@Override
	public void onInitializeClient() {

		ClientLoginNetworking.registerGlobalReceiver(OriginOrbs.PRESENCE_CHANNEL, (client, handler, buf, listenerAdder) -> {
			return CompletableFuture.completedFuture(new PacketByteBuf(Unpooled.buffer()));
		});

        ClientPlayNetworking.registerGlobalReceiver(OriginOrbs.UPDATE_ORIGIN_ORBS_PACKET_ID, (client, handler, buf, responseSender) -> {
			int orb_count = buf.readInt();
            for (int i = 0; i < orb_count; i++) {
                ItemStack stack = buf.readItemStack();
                Identifier id = buf.readIdentifier();
                Registry.register(Registry.ITEM, id, stack.getItem());
            }
		});
        
		ClientLoginNetworking.registerGlobalReceiver(OriginOrbs.UPDATE_ORIGIN_ORBS_PACKET_ID, (client, handler, buf, responseSender) -> {
			int orb_count = buf.readInt();
            for (int i = 0; i < orb_count; i++) {
                ItemStack stack = buf.readItemStack();
                Identifier id = buf.readIdentifier();
                Registry.register(Registry.ITEM, id, stack.getItem());
            }
            return CompletableFuture.completedFuture(new PacketByteBuf(Unpooled.buffer()));
		});

	}
}
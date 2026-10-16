/*
 * Copyright 2021-2023 Siphalor
 * Modified 2026 by traelmyx: ported to Minecraft 1.21.1 (Mojang mappings, NeoForge and Fabric).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.wanderingcollector.mixin;

import de.siphalor.wanderingcollector.LostItemStorage;
import de.siphalor.wanderingcollector.WCConfig;
import de.siphalor.wanderingcollector.WanderingCollector;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

@Mixin(WanderingTrader.class)
public abstract class MixinWanderingTraderEntity extends AbstractVillager implements IWanderingTraderEntity {
	@Unique
	private final Map<UUID, Collection<MerchantOffer>> playerSpecificTrades = new HashMap<>();

	public MixinWanderingTraderEntity(EntityType<? extends AbstractVillager> entityType, Level world) {
		super(entityType, world);
	}

	@Override
	public MerchantOffers wandering_collector$getOffers(Player player) {
		MerchantOffers offers = new MerchantOffers();
		offers.addAll(getOffers());
		if (player == null) {
			return offers;
		}
		Collection<MerchantOffer> own = playerSpecificTrades.get(player.getUUID());
		if (own == null) {
			if (!(player instanceof IServerPlayerEntity serverPlayer) || WCConfig.get().buyBackTrades <= 0
					|| serverPlayer.wandering_collector$getLostItemStorage().isEmpty()) {
				own = Collections.emptyList();
			} else {
				LostItemStorage storage = serverPlayer.wandering_collector$getLostItemStorage();
				own = new ArrayList<>(WCConfig.get().buyBackTrades);
				for (ItemStack stack : storage.poll(WCConfig.get().buyBackTrades)) {
					ItemStack price = WCConfig.get().defaultPrices.getPriceStack(stack);
					own.add(new MerchantOffer(new ItemCost(price.getItem(), price.getCount()), stack, 1, 1, 1F));
				}
			}
			playerSpecificTrades.put(player.getUUID(), own);
		}
		offers.addAll(own);
		return offers;
	}

	// Merchant#openTradingScreen, but with this player's own offers appended.
	@Override
	public void openTradingScreen(Player player, Component name, int level) {
		OptionalInt containerId = player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new MerchantMenu(id, inventory, this), name));
		if (containerId.isPresent()) {
			MerchantOffers offers = wandering_collector$getOffers(player);
			if (!offers.isEmpty()) {
				player.sendMerchantOffers(containerId.getAsInt(), offers, level, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
			}
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void readCustomDataFromTagInject(CompoundTag tag, CallbackInfo callbackInfo) {
		playerSpecificTrades.clear();
		if (tag.contains(WanderingCollector.PLAYER_SPECIFIC_TRADES, Tag.TAG_COMPOUND)) {
			var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			CompoundTag compound = tag.getCompound(WanderingCollector.PLAYER_SPECIFIC_TRADES);
			for (String key : compound.getAllKeys()) {
				ListTag list = compound.getList(key, Tag.TAG_COMPOUND);
				ArrayList<MerchantOffer> offers = new ArrayList<>(list.size());
				for (Tag tradeTag : list) {
					MerchantOffer.CODEC.parse(ops, tradeTag).resultOrPartial(WanderingCollector.LOGGER::warn).ifPresent(offers::add);
				}
				try {
					playerSpecificTrades.put(UUID.fromString(key), offers);
				} catch (IllegalArgumentException ignored) {
				}
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo callbackInfo) {
		var ops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		CompoundTag compound = new CompoundTag();
		for (Map.Entry<UUID, Collection<MerchantOffer>> entry : playerSpecificTrades.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			ListTag list = new ListTag();
			for (MerchantOffer offer : entry.getValue()) {
				MerchantOffer.CODEC.encodeStart(ops, offer).resultOrPartial(WanderingCollector.LOGGER::warn).ifPresent(list::add);
			}
			compound.put(entry.getKey().toString(), list);
		}
		tag.put(WanderingCollector.PLAYER_SPECIFIC_TRADES, compound);
	}
}

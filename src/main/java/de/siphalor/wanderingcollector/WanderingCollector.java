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

package de.siphalor.wanderingcollector;

import de.siphalor.wanderingcollector.util.IItemEntity;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WanderingCollector {
	public static final Logger LOGGER = LoggerFactory.getLogger("Wandering Collector");
	public static final String MOD_ID = "wandering_collector";
	public static final String LOST_STACKS_KEY = MOD_ID + ":" + "lost_stacks";
	public static final String PLAYER_SPECIFIC_TRADES = MOD_ID + ":" + "player_specific_trades";
	/** Items that are never remembered (emeralds by default, so the trader can't sell you your own currency). */
	public static final TagKey<Item> DENY_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "deny"));

	/** Called when an item entity is destroyed for good: despawned, burnt, blown up or fallen out of the world. */
	public static void addStackToThrower(ItemEntity item) {
		if (item.getItem().isEmpty() || item.getItem().is(DENY_TAG) || !(item.level() instanceof ServerLevel level)) {
			return;
		}
		Entity theFormerOwner = null;
		if (WCConfig.get().includeDroppedStacks) {
			theFormerOwner = item.getOwner();
		}
		if (theFormerOwner == null && item instanceof IItemEntity itemEntity) {
			UUID ownerUuid = itemEntity.wanderingCollector$getFormerOwner();
			if (ownerUuid != null) {
				theFormerOwner = level.getServer().getPlayerList().getPlayer(ownerUuid);
			}
		}
		if (theFormerOwner instanceof IServerPlayerEntity player) {
			player.wandering_collector$getLostItemStorage().add(item.getItem().copy());
		}
	}
}

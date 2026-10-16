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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.siphalor.wanderingcollector.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class WCConfig {
	private static final Random random = new Random();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static WCConfig instance = new WCConfig();

	/** Also remember items you threw away yourself, not only the ones you lost on death. */
	public boolean includeDroppedStacks = false;
	/** How many lost stacks are remembered per player; the oldest are forgotten first. */
	public int maxLostStackAmount = 64;
	/** Which lost stacks a Wandering Trader offers first: NEWEST, OLDEST or RANDOM. */
	public LostItemStorage.PollMode offerCreation = LostItemStorage.PollMode.RANDOM;
	/** Merge lost stacks of the same item where they fit. */
	public boolean combineLostStacks = true;
	/** How many of your lost stacks each Wandering Trader offers you. */
	public int buyBackTrades = 2;
	public PriceDefinition defaultPrices = new PriceDefinition();

	public static WCConfig get() {
		return instance;
	}

	public static void load(Path configDir) {
		Path file = configDir.resolve(WanderingCollector.MOD_ID + ".json");
		try {
			if (Files.exists(file)) {
				WCConfig read = GSON.fromJson(Files.readString(file), WCConfig.class);
				if (read != null) {
					read.validate();
					instance = read;
				}
			} else {
				Files.createDirectories(configDir);
				Files.writeString(file, GSON.toJson(instance));
			}
		} catch (IOException | RuntimeException e) {
			WanderingCollector.LOGGER.error("Could not read {}, using defaults", file, e);
		}
	}

	private void validate() {
		maxLostStackAmount = Math.max(0, maxLostStackAmount);
		buyBackTrades = Math.max(0, buyBackTrades);
		if (offerCreation == null) offerCreation = LostItemStorage.PollMode.RANDOM;
		if (defaultPrices == null) defaultPrices = new PriceDefinition();
		defaultPrices.minPrice = Mth.clamp(defaultPrices.minPrice, 1, 64);
		defaultPrices.maxPrice = Mth.clamp(defaultPrices.maxPrice, 1, 64);
		defaultPrices.minPriceScale = Mth.clamp(defaultPrices.minPriceScale, 0f, 1f);
		if (defaultPrices.priceItem == null) defaultPrices.priceItem = "minecraft:emerald";
	}

	public static class PriceDefinition {
		public int minPrice = 32;
		public int maxPrice = 64;
		public String priceItem = "minecraft:emerald";
		/** Charge less for partial stacks. */
		public boolean scalePriceWithCount = true;
		/** The fraction of the price a single item costs when scaling with count. */
		public float minPriceScale = 0.3f;

		public ItemStack getPriceStack(ItemStack forStack) {
			ResourceLocation id = ResourceLocation.tryParse(this.priceItem);
			Item priceItem = id == null ? Items.AIR : BuiltInRegistries.ITEM.get(id);
			if (priceItem == Items.AIR) {
				WanderingCollector.LOGGER.warn("Unknown price item \"{}\", defaulting to minecraft:emerald", this.priceItem);
				priceItem = Items.EMERALD;
			}
			int count = Utils.randInclusive(random, this.minPrice, this.maxPrice);
			if (scalePriceWithCount) {
				count = Mth.ceil(count * (minPriceScale + (1 - minPriceScale) * forStack.getCount() / forStack.getMaxStackSize()));
			}
			return new ItemStack(priceItem, Mth.clamp(count, 1, 64));
		}
	}
}

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

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LostItemStorage {
	private static final Random RANDOM = new Random();
	private final List<ItemStack> stacks = new ArrayList<>();

	public void read(CompoundTag parentNbt, HolderLookup.Provider registries) {
		stacks.clear();
		if (parentNbt.contains(WanderingCollector.LOST_STACKS_KEY, Tag.TAG_COMPOUND)) {
			CompoundTag ownNbt = parentNbt.getCompound(WanderingCollector.LOST_STACKS_KEY);
			ListTag listNbt = ownNbt.getList("Stacks", Tag.TAG_COMPOUND);
			for (Tag stackNbt : listNbt) {
				ItemStack.parse(registries, stackNbt).ifPresent(stacks::add);
			}
		}
	}

	public void write(CompoundTag parentNbt, HolderLookup.Provider registries) {
		if (stacks.isEmpty()) {
			return;
		}
		CompoundTag ownNbt = new CompoundTag();
		ListTag listNbt = new ListTag();
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) listNbt.add(stack.save(registries));
		}
		ownNbt.put("Stacks", listNbt);
		parentNbt.put(WanderingCollector.LOST_STACKS_KEY, ownNbt);
	}

	public boolean isEmpty() {
		return stacks.isEmpty();
	}

	public List<ItemStack> view() {
		return List.copyOf(stacks);
	}

	public void add(ItemStack newStack) {
		if (WCConfig.get().combineLostStacks && tryCombine(newStack)) {
			return;
		}
		if (WCConfig.get().maxLostStackAmount <= 0) {
			return;
		}
		while (stacks.size() >= WCConfig.get().maxLostStackAmount) {
			stacks.remove(0);
		}
		stacks.add(newStack);
	}

	private boolean tryCombine(ItemStack newStack) {
		int requiredSpace = newStack.getCount();
		int space = 0;
		List<ItemStack> equalStacks = new ArrayList<>();
		for (ItemStack stack : stacks) {
			if (ItemStack.isSameItemSameComponents(stack, newStack)) {
				equalStacks.add(stack);
				space += stack.getMaxStackSize() - stack.getCount();
				if (space >= requiredSpace) {
					break;
				}
			}
		}
		if (space < requiredSpace) {
			return false;
		}
		for (ItemStack equalStack : equalStacks) {
			int move = Math.min(equalStack.getMaxStackSize() - equalStack.getCount(), requiredSpace);
			equalStack.grow(move);
			requiredSpace -= move;
			if (requiredSpace <= 0) {
				break;
			}
		}
		return true;
	}

	public Collection<ItemStack> poll(int stackCount) {
		stackCount = Math.min(stackCount, stacks.size());
		List<ItemStack> result = new ArrayList<>(stackCount);
		switch (WCConfig.get().offerCreation) {
			case NEWEST -> {
				List<ItemStack> range = stacks.subList(stacks.size() - stackCount, stacks.size());
				result.addAll(range);
				range.clear();
			}
			case OLDEST -> {
				List<ItemStack> range = stacks.subList(0, stackCount);
				result.addAll(range);
				range.clear();
			}
			case RANDOM -> {
				for (int i = 0; i < stackCount; i++) {
					int randIndex = RANDOM.nextInt(stacks.size());
					result.add(stacks.remove(randIndex));
				}
			}
		}
		return result;
	}

	public enum PollMode {NEWEST, OLDEST, RANDOM}
}

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

import de.siphalor.wanderingcollector.util.IItemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.21.11 {
/*@Mixin(net.minecraft.world.entity.LivingEntity.class)
public class MixinPlayer {
	// Items dropped without the player's name on them (death drops, items spilling out of a closed screen) remember who lost them.
	@Inject(method = "createItemStackToDrop", at = @At("RETURN"))
	public void onItemDropped(ItemStack itemStack, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
		if (!includeThrowerName && cir.getReturnValue() instanceof IItemEntity item && (Object) this instanceof ServerPlayer player) {
			item.wanderingCollector$setFormerOwner(player.getUUID());
		}
	}
}
*///?} else {
@Mixin(Player.class)
public class MixinPlayer {
	// Items dropped without the player's name on them (death drops, items spilling out of a closed screen) remember who lost them.
	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
	public void onItemDropped(ItemStack itemStack, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
		if (!includeThrowerName && cir.getReturnValue() instanceof IItemEntity item && (Object) this instanceof ServerPlayer player) {
			item.wanderingCollector$setFormerOwner(player.getUUID());
		}
	}
}
//?}

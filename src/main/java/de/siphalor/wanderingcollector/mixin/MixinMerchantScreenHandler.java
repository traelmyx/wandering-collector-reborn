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

import de.siphalor.wanderingcollector.util.IMerchantInventory;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class)
public abstract class MixinMerchantScreenHandler {
	@Shadow @Final private MerchantContainer tradeContainer;
	@Shadow @Final private Merchant trader;

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("RETURN"))
	public void onInit(int containerId, Inventory playerInventory, Merchant merchant, CallbackInfo callbackInfo) {
		((IMerchantInventory) tradeContainer).wandering_collector$setPlayer(playerInventory.player);
	}

	@Inject(method = "getOffers", at = @At("HEAD"), cancellable = true)
	public void getOffersInject(CallbackInfoReturnable<MerchantOffers> cir) {
		if (trader instanceof IWanderingTraderEntity wanderer && trader.getTradingPlayer() != null && !trader.getTradingPlayer().level().isClientSide) {
			cir.setReturnValue(wanderer.wandering_collector$getOffers(trader.getTradingPlayer()));
		}
	}
}

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

import de.siphalor.wanderingcollector.WanderingCollector;
import de.siphalor.wanderingcollector.util.IItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements IItemEntity {
	@Unique
	private static final String formerOwnerKey = WanderingCollector.MOD_ID + ":FormerOwner";
	@Unique
	private UUID formerOwner;

	public MixinItemEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	public UUID wanderingCollector$getFormerOwner() {
		return formerOwner;
	}

	@Override
	public void wanderingCollector$setFormerOwner(UUID playerUuid) {
		this.formerOwner = playerUuid;
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		if (tag.hasUUID(formerOwnerKey)) {
			formerOwner = tag.getUUID(formerOwnerKey);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void writeCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		if (formerOwner != null) {
			tag.putUUID(formerOwnerKey, formerOwner);
		}
	}

	// The only discard of a non-empty item inside tick() is the despawn.
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
	public void tickInject(CallbackInfo callbackInfo) {
		WanderingCollector.addStackToThrower((ItemEntity) (Object) this);
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
	public void onDeathInject(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		WanderingCollector.addStackToThrower((ItemEntity) (Object) this);
	}
}

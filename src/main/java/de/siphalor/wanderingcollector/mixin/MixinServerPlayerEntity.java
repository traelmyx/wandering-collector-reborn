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
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerEntity implements IServerPlayerEntity {
	@Unique
	private LostItemStorage lostItemStorage = new LostItemStorage();

	@Inject(method = "restoreFrom", at = @At("RETURN"))
	public void copyFromInject(ServerPlayer other, boolean alive, CallbackInfo callbackInfo) {
		lostItemStorage = ((IServerPlayerEntity) other).wandering_collector$getLostItemStorage();
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void readCustomDataFromTagInject(CompoundTag tag, CallbackInfo callbackInfo) {
		lostItemStorage.read(tag, ((ServerPlayer) (Object) this).registryAccess());
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void writeCustomDataToTagInject(CompoundTag tag, CallbackInfo callbackInfo) {
		lostItemStorage.write(tag, ((ServerPlayer) (Object) this).registryAccess());
	}

	@Override
	public LostItemStorage wandering_collector$getLostItemStorage() {
		return lostItemStorage;
	}
}

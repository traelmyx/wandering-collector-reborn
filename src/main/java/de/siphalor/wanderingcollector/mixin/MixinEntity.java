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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
	@Inject(method = "onBelowWorld", at = @At("HEAD"))
	public void onVoidRemove(CallbackInfo ci) {
		if ((Object) this instanceof ItemEntity item) {
			WanderingCollector.addStackToThrower(item);
		}
	}
}

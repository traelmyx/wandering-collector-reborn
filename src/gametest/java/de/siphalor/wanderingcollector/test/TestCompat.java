package de.siphalor.wanderingcollector.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.11 {
/*import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
*///?} else {
import net.minecraft.world.entity.npc.WanderingTrader;
//?}

// Version differences the game tests run into, in one place.
public final class TestCompat {
    private TestCompat() {
    }

    public static EntityType<WanderingTrader> wanderingTraderType() {
        //? if >=26 {
        /*return net.minecraft.world.entity.EntityTypes.WANDERING_TRADER;
        *///?} else {
        return EntityType.WANDERING_TRADER;
        //?}
    }

    public static WanderingTrader newWanderingTrader(GameTestHelper helper) {
        //? if >=1.21.11 {
        /*return wanderingTraderType().create(helper.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD);
        *///?} else {
        return wanderingTraderType().create(helper.getLevel());
        //?}
    }

    /** Drops a stack the way the game does: on death (no thrower name) or deliberately (with it). */
    public static ItemEntity drop(ServerPlayer player, ItemStack stack, boolean onDeath) {
        //? if >=26.3 {
        /*ItemEntity item = player.createItemStackToDrop(stack, onDeath, !onDeath);
        if (item != null) player.level().addFreshEntity(item);
        return item;
        *///?} else {
        return player.drop(stack, onDeath, !onDeath);
        //?}
    }

    public static void burn(GameTestHelper helper, ItemEntity item) {
        //? if >=1.21.2 {
        /*item.hurtServer(helper.getLevel(), helper.getLevel().damageSources().lava(), 100);
        *///?} else {
        item.hurt(helper.getLevel().damageSources().lava(), 100);
        //?}
    }

    public static CompoundTag save(GameTestHelper helper, Entity entity) {
        //? if >=1.21.6 {
        /*var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
        *///?} else {
        CompoundTag tag = new CompoundTag();
        entity.saveWithoutId(tag);
        return tag;
        //?}
    }

    public static void load(GameTestHelper helper, Entity entity, CompoundTag tag) {
        //? if >=1.21.6 {
        /*entity.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        *///?} else {
        entity.load(tag);
        //?}
    }
}

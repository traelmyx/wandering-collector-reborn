package de.siphalor.wanderingcollector.test;

import de.siphalor.wanderingcollector.LostItemStorage;
import de.siphalor.wanderingcollector.util.IItemEntity;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class CollectorTests {
    private CollectorTests() {
    }

    private static LostItemStorage storage(ServerPlayer player) {
        return ((IServerPlayerEntity) player).wandering_collector$getLostItemStorage();
    }

    private static ItemEntity lostItem(GameTestHelper helper, ServerPlayer owner, ItemStack stack) {
        Vec3 at = helper.absoluteVec(new Vec3(1.5, 2.5, 1.5));
        ItemEntity item = new ItemEntity(helper.getLevel(), at.x, at.y, at.z, stack);
        ((IItemEntity) item).wanderingCollector$setFormerOwner(owner.getUUID());
        item.setNeverPickUp();
        helper.getLevel().addFreshEntity(item);
        return item;
    }

    public static void deathDropsRememberTheirOwner(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemEntity lost = player.drop(new ItemStack(Items.DIAMOND_PICKAXE), true, false);
        ItemEntity thrown = player.drop(new ItemStack(Items.STICK), false, true);
        helper.assertTrue(lost != null && player.getUUID().equals(((IItemEntity) lost).wanderingCollector$getFormerOwner()), "death-style drop has no former owner");
        helper.assertTrue(thrown != null && ((IItemEntity) thrown).wanderingCollector$getFormerOwner() == null, "a deliberately thrown item was marked as lost");
        lost.discard();
        thrown.discard();
        helper.succeed();
    }

    public static void burntItemsAreRemembered(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemEntity item = lostItem(helper, player, new ItemStack(Items.IRON_SWORD));
        item.hurt(helper.getLevel().damageSources().lava(), 100);
        helper.assertFalse(item.isAlive(), "item survived the lava");
        helper.assertTrue(storage(player).view().stream().anyMatch(s -> s.is(Items.IRON_SWORD)), "burnt sword not remembered: " + storage(player).view());
        helper.succeed();
    }

    public static void despawnedItemsAreRemembered(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemEntity item = lostItem(helper, player, new ItemStack(Items.GOLDEN_APPLE, 3));
        CompoundTag tag = new CompoundTag();
        item.saveWithoutId(tag);
        tag.putShort("Age", (short) 5990);
        item.load(tag);
        helper.succeedWhen(() -> {
            helper.assertFalse(item.isAlive(), "item has not despawned yet");
            helper.assertTrue(storage(player).view().stream().anyMatch(s -> s.is(Items.GOLDEN_APPLE) && s.getCount() == 3), "despawned apples not remembered: " + storage(player).view());
        });
    }

    public static void deniedItemsAreIgnored(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemEntity item = lostItem(helper, player, new ItemStack(Items.EMERALD, 10));
        item.hurt(helper.getLevel().damageSources().lava(), 100);
        helper.assertTrue(storage(player).isEmpty(), "emeralds (deny tag) were remembered");
        helper.succeed();
    }

    public static void stacksCombine(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        storage(player).add(new ItemStack(Items.COBBLESTONE, 10));
        storage(player).add(new ItemStack(Items.COBBLESTONE, 20));
        List<ItemStack> stacks = storage(player).view();
        helper.assertTrue(stacks.size() == 1 && stacks.get(0).getCount() == 30, "cobblestone not combined: " + stacks);
        helper.succeed();
    }

    public static void traderSellsThemBackOnce(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ServerPlayer other = helper.makeMockServerPlayerInLevel();
        storage(player).add(new ItemStack(Items.DIAMOND_CHESTPLATE));
        storage(player).add(new ItemStack(Items.ELYTRA));
        storage(player).add(new ItemStack(Items.TRIDENT));
        WanderingTrader trader = helper.spawnWithNoFreeWill(EntityType.WANDERING_TRADER, new BlockPos(1, 2, 1));
        int normal = trader.getOffers().size();
        MerchantOffers offers = ((IWanderingTraderEntity) trader).wandering_collector$getOffers(player);
        List<MerchantOffer> mine = offers.subList(normal, offers.size());
        helper.assertTrue(mine.size() == 2, "expected 2 buy-back offers, got " + mine.size());
        for (MerchantOffer offer : mine) {
            helper.assertTrue(offer.getMaxUses() == 1 && offer.getBaseCostA().is(Items.EMERALD)
                && offer.getBaseCostA().getCount() >= 1 && offer.getBaseCostA().getCount() <= 64, "bad offer " + offer.getResult() + " for " + offer.getBaseCostA());
        }
        helper.assertTrue(storage(player).view().size() == 1, "the offered stacks are still in storage");
        MerchantOffers again = ((IWanderingTraderEntity) trader).wandering_collector$getOffers(player);
        helper.assertTrue(again.size() == offers.size() && again.get(normal) == offers.get(normal), "offers changed on the second look");
        helper.assertTrue(((IWanderingTraderEntity) trader).wandering_collector$getOffers(other).size() == normal, "another player was offered someone else's items");

        CompoundTag saved = new CompoundTag();
        trader.saveWithoutId(saved);
        WanderingTrader copy = EntityType.WANDERING_TRADER.create(helper.getLevel());
        copy.load(saved);
        MerchantOffers restored = ((IWanderingTraderEntity) copy).wandering_collector$getOffers(player);
        helper.assertTrue(restored.size() == offers.size() && ItemStack.matches(restored.get(normal).getResult(), offers.get(normal).getResult()), "buy-back offers lost on save/load");
        trader.discard();
        helper.succeed();
    }

    public static void lostItemsSurviveSaving(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        storage(player).add(new ItemStack(Items.NETHERITE_SWORD));
        CompoundTag tag = new CompoundTag();
        player.addAdditionalSaveData(tag);
        LostItemStorage read = new LostItemStorage();
        read.read(tag, helper.getLevel().registryAccess());
        helper.assertTrue(read.view().size() == 1 && read.view().get(0).is(Items.NETHERITE_SWORD), "lost items lost on save/load: " + tag);
        helper.succeed();
    }
}

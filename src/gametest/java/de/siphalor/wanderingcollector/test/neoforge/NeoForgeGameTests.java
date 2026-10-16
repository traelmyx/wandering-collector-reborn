package de.siphalor.wanderingcollector.test.neoforge;

//? if neoforge {
import de.siphalor.wanderingcollector.test.ClientSmoke;
import de.siphalor.wanderingcollector.test.CollectorTests;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(NeoForgeGameTests.MOD_ID)
@PrefixGameTestTemplate(false)
@Mod(NeoForgeGameTests.MOD_ID)
public class NeoForgeGameTests {
    public static final String MOD_ID = "wandering_collector_gametest";

    public NeoForgeGameTests(Dist dist) {
        if (dist.isClient() && ClientSmoke.start()) {
            NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> ClientSmoke.tick(net.minecraft.client.Minecraft.getInstance()));
        }
    }

    @GameTest(template = "empty")
    public static void deathDropsRememberTheirOwner(GameTestHelper helper) {
        CollectorTests.deathDropsRememberTheirOwner(helper);
    }

    @GameTest(template = "empty")
    public static void burntItemsAreRemembered(GameTestHelper helper) {
        CollectorTests.burntItemsAreRemembered(helper);
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void despawnedItemsAreRemembered(GameTestHelper helper) {
        CollectorTests.despawnedItemsAreRemembered(helper);
    }

    @GameTest(template = "empty")
    public static void deniedItemsAreIgnored(GameTestHelper helper) {
        CollectorTests.deniedItemsAreIgnored(helper);
    }

    @GameTest(template = "empty")
    public static void stacksCombine(GameTestHelper helper) {
        CollectorTests.stacksCombine(helper);
    }

    @GameTest(template = "empty")
    public static void traderSellsThemBackOnce(GameTestHelper helper) {
        CollectorTests.traderSellsThemBackOnce(helper);
    }

    @GameTest(template = "empty")
    public static void lostItemsSurviveSaving(GameTestHelper helper) {
        CollectorTests.lostItemsSurviveSaving(helper);
    }
}
//?}

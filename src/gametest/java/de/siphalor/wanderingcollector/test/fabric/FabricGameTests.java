package de.siphalor.wanderingcollector.test.fabric;

//? if fabric {
/*import de.siphalor.wanderingcollector.test.CollectorTests;
import net.minecraft.gametest.framework.GameTestHelper;
//? if >=1.21.5 {
import net.fabricmc.fabric.api.gametest.v1.GameTest;
//?} else {
/^import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
^///?}

//? if >=1.21.5 {
public class FabricGameTests {
	private static final String EMPTY = "wandering_collector_gametest:empty";

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void deathDropsRememberTheirOwner(GameTestHelper helper) {
		CollectorTests.deathDropsRememberTheirOwner(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void burntItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.burntItemsAreRemembered(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void despawnedItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.despawnedItemsAreRemembered(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void deniedItemsAreIgnored(GameTestHelper helper) {
		CollectorTests.deniedItemsAreIgnored(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void stacksCombine(GameTestHelper helper) {
		CollectorTests.stacksCombine(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void traderSellsThemBackOnce(GameTestHelper helper) {
		CollectorTests.traderSellsThemBackOnce(helper);
	}

	@GameTest(structure = EMPTY, maxTicks = 100)
	public void lostItemsSurviveSaving(GameTestHelper helper) {
		CollectorTests.lostItemsSurviveSaving(helper);
	}
}
//?} else {
/^public class FabricGameTests implements FabricGameTest {
	private static final String EMPTY = "wandering_collector_gametest:empty";

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void deathDropsRememberTheirOwner(GameTestHelper helper) {
		CollectorTests.deathDropsRememberTheirOwner(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void burntItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.burntItemsAreRemembered(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void despawnedItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.despawnedItemsAreRemembered(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void deniedItemsAreIgnored(GameTestHelper helper) {
		CollectorTests.deniedItemsAreIgnored(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void stacksCombine(GameTestHelper helper) {
		CollectorTests.stacksCombine(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void traderSellsThemBackOnce(GameTestHelper helper) {
		CollectorTests.traderSellsThemBackOnce(helper);
	}

	@GameTest(template = EMPTY, timeoutTicks = 100)
	public void lostItemsSurviveSaving(GameTestHelper helper) {
		CollectorTests.lostItemsSurviveSaving(helper);
	}
}
^///?}
*///?}

package de.siphalor.wanderingcollector.test.neoforge;

//? if neoforge {
import de.siphalor.wanderingcollector.test.ClientSmoke;
import de.siphalor.wanderingcollector.test.CollectorTests;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if >=1.21.5 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
*///?} else {
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
//?}

//? if <1.21.5 {
@GameTestHolder(NeoForgeGameTests.MOD_ID)
@PrefixGameTestTemplate(false)
//?}
@Mod(NeoForgeGameTests.MOD_ID)
public class NeoForgeGameTests {
	public static final String MOD_ID = "wandering_collector_gametest";

	public NeoForgeGameTests(IEventBus modBus, Dist dist) {
		//? if >=1.21.5 {
		/*FUNCTIONS.register(modBus);
		modBus.addListener(NeoForgeGameTests::registerTests);
		*///?}
		if (dist.isClient() && ClientSmoke.start()) {
			NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> ClientSmoke.tick(net.minecraft.client.Minecraft.getInstance()));
		}
	}

	//? if >=1.21.5 {
	/*private static final DeferredRegister<Consumer<GameTestHelper>> FUNCTIONS = DeferredRegister.create(Registries.TEST_FUNCTION, MOD_ID);
	private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(MOD_ID, "empty");

	static {
		FUNCTIONS.register("death_drops_remember_their_owner", () -> CollectorTests::deathDropsRememberTheirOwner);
		FUNCTIONS.register("burnt_items_are_remembered", () -> CollectorTests::burntItemsAreRemembered);
		FUNCTIONS.register("despawned_items_are_remembered", () -> CollectorTests::despawnedItemsAreRemembered);
		FUNCTIONS.register("denied_items_are_ignored", () -> CollectorTests::deniedItemsAreIgnored);
		FUNCTIONS.register("stacks_combine", () -> CollectorTests::stacksCombine);
		FUNCTIONS.register("trader_sells_them_back_once", () -> CollectorTests::traderSellsThemBackOnce);
		FUNCTIONS.register("lost_items_survive_saving", () -> CollectorTests::lostItemsSurviveSaving);
	}

	private static void registerTests(RegisterGameTestsEvent event) {
		//? if >=26 {
		Holder<TestEnvironmentDefinition<?>> env = event.registerEnvironment(Identifier.fromNamespaceAndPath(MOD_ID, "default"));
		//?} else
		/^Holder<TestEnvironmentDefinition> env = event.registerEnvironment(Identifier.fromNamespaceAndPath(MOD_ID, "default"));^/
		register(event, env, "death_drops_remember_their_owner", 100, 1, 1);
		register(event, env, "burnt_items_are_remembered", 100, 1, 1);
		register(event, env, "despawned_items_are_remembered", 100, 1, 1);
		register(event, env, "denied_items_are_ignored", 100, 1, 1);
		register(event, env, "stacks_combine", 100, 1, 1);
		register(event, env, "trader_sells_them_back_once", 100, 1, 1);
		register(event, env, "lost_items_survive_saving", 100, 1, 1);
	}

	//? if >=26 {
	private static void register(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> env, String name, int maxTicks, int attempts, int successes) {
	//?} else
	/^private static void register(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition> env, String name, int maxTicks, int attempts, int successes) {^/
		Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
		event.registerTest(id, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id),
			//? if >=26.3 {
			new TestData<>(env, net.minecraft.world.level.Level.OVERWORLD, EMPTY, maxTicks, 0, true,
				net.minecraft.world.level.block.Rotation.NONE, false, attempts, successes, false, 0)));
			//?} else if >=26 {
			/^new TestData<>(env, EMPTY, maxTicks, 0, true,
				net.minecraft.world.level.block.Rotation.NONE, false, attempts, successes, false, 0)));
			^///?} else {
			/^new TestData<>(env, EMPTY, maxTicks, 0, true,
				net.minecraft.world.level.block.Rotation.NONE, false, attempts, successes, false)));
			^///?}
	}
	*///?} else {
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void deathDropsRememberTheirOwner(GameTestHelper helper) {
		CollectorTests.deathDropsRememberTheirOwner(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void burntItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.burntItemsAreRemembered(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void despawnedItemsAreRemembered(GameTestHelper helper) {
		CollectorTests.despawnedItemsAreRemembered(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void deniedItemsAreIgnored(GameTestHelper helper) {
		CollectorTests.deniedItemsAreIgnored(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void stacksCombine(GameTestHelper helper) {
		CollectorTests.stacksCombine(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void traderSellsThemBackOnce(GameTestHelper helper) {
		CollectorTests.traderSellsThemBackOnce(helper);
	}

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void lostItemsSurviveSaving(GameTestHelper helper) {
		CollectorTests.lostItemsSurviveSaving(helper);
	}
	//?}
}
//?}

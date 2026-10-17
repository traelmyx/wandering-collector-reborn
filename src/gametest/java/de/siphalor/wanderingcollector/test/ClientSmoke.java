package de.siphalor.wanderingcollector.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.GameType;

import net.minecraft.client.gui.screens.inventory.MerchantScreen;
//? if >=1.21.11 {
/*import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
*///?} else {
import net.minecraft.world.entity.npc.WanderingTrader;
//?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.nio.file.Files;

// Run with -Dsmoketest=true: creates a flat world, takes a screenshot and quits. Exit code 1 on failure.
public final class ClientSmoke {

    public static final boolean ENABLED = Boolean.getBoolean("smoketest");
    private static final long TIMEOUT_MS = 5 * 60 * 1000;

    private static int stage;
    private static int ticks;

    private ClientSmoke() {
    }

    public static boolean start() {
        if (!ENABLED) {
            return false;
        }
        Thread watchdog = new Thread(() -> {
            try {
                Thread.sleep(TIMEOUT_MS);
                fail("timed out");
            } catch (InterruptedException ignored) {
            }
        }, "smoketest-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        return true;
    }

    public static void tick(Minecraft mc) {
        try {
            switch (stage) {
                case 0 -> {
                    Screen screen = SmokeCompat.screen(mc);
                    if (screen != null && SmokeCompat.overlayHidden(mc) && ++ticks >= 20) {
                        if (screen.getClass().getSimpleName().equals("LoadingErrorScreen")) {
                            fail("mod loading errors");
                        }
                        ticks = 0;
                        mc.options.tutorialStep = net.minecraft.client.tutorial.TutorialSteps.NONE;
                        SmokeCompat.createFlatWorld(mc, GameType.SURVIVAL);
                        stage = 1;
                    }
                }
                case 1 -> {
                    if (mc.level != null && mc.player != null && SmokeCompat.screen(mc) == null) {
                        stage = 2;
                    }
                }
                case 2 -> {
                    ticks++;
                    if (ticks == 40) {
                        setup(mc);
                    }
                    if (ticks == 80) {
                        // open the trade screen as if the player had right-clicked the trader
                        var server = mc.getSingleplayerServer();
                        server.execute(() -> {
                            var player = server.getPlayerList().getPlayers().get(0);
                            var trader = player.level().getEntitiesOfClass(WanderingTrader.class, player.getBoundingBox().inflate(6)).stream().findFirst().orElse(null);
                            if (trader == null) fail("no wandering trader near the player");
                            trader.setTradingPlayer(player);
                            trader.openTradingScreen(player, trader.getDisplayName(), 1);
                        });
                    }
                    if (ticks == 120) {
                        SmokeCompat.screenshot(mc, "buy-back.png");
                    }
                    if (ticks >= 130) {
                        if (!(SmokeCompat.screen(mc) instanceof MerchantScreen screen)) {
                            fail("trade screen not open: " + SmokeCompat.screen(mc));
                            return;
                        }
                        boolean back = screen.getMenu().getOffers().stream().anyMatch(o -> o.getResult().is(Items.ELYTRA));
                        if (!back) fail("the trader does not offer the lost elytra back");
                        SmokeCompat.setScreen(mc, null);
                        Files.writeString(mc.gameDirectory.toPath().resolve("smoke-ok"), "ok\n");
                        stage = 3;
                        mc.stop();
                    }
                }
                default -> {
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }

    // the player "lost" an elytra and a diamond pickaxe earlier; a wandering trader stands in front of them
    private static void setup(Minecraft mc) {
        var server = mc.getSingleplayerServer();
        server.execute(() -> {
            var player = server.getPlayerList().getPlayers().get(0);
            var storage = ((de.siphalor.wanderingcollector.util.IServerPlayerEntity) player).wandering_collector$getLostItemStorage();
            storage.add(new ItemStack(Items.ELYTRA));
            var pick = new ItemStack(Items.DIAMOND_PICKAXE);
            pick.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("Old Faithful"));
            storage.add(pick);
            var look = player.getLookAngle();
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), String.format(java.util.Locale.ROOT,
                "summon minecraft:wandering_trader %.2f %.2f %.2f {NoAI:1b,Tags:[\"smoke\"]}", player.getX() + look.x * 2, player.getY(), player.getZ() + look.z * 2));
        });
    }

    public static void fail(String why) {
        System.err.println("Smoke test failed: " + why);
        System.out.println("[clientsmoke] FAILED: " + why);
        System.err.flush();
        Runtime.getRuntime().halt(1);
    }
}

package de.siphalor.wanderingcollector.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
//? if >=1.21.11 && <26 {
/*import net.minecraft.world.level.gamerules.GameRules;
*///?} else if <1.21.11 {
import net.minecraft.world.level.GameRules;
//?}

// What the client smoke test needs from the game, for every Minecraft version this mod is built for.
public final class SmokeCompat {
    private SmokeCompat() {
    }

    public static Screen screen(Minecraft mc) {
        //? if >=26 {
        /*return mc.gui.screen();
        *///?} else {
        return mc.screen;
        //?}
    }

    public static void setScreen(Minecraft mc, Screen screen) {
        //? if >=26 {
        /*mc.gui.setScreen(screen);
        *///?} else {
        mc.setScreen(screen);
        //?}
    }

    public static boolean overlayHidden(Minecraft mc) {
        //? if >=26 {
        /*return mc.gui.overlay() == null;
        *///?} else {
        return mc.getOverlay() == null;
        //?}
    }

    public static void createFlatWorld(Minecraft mc, GameType type) {
        //? if >=26 {
        /*LevelSettings settings = new LevelSettings("smoke", type,
            new LevelSettings.DifficultySettings(Difficulty.PEACEFUL, false, false), true, WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel("smoke", settings, new WorldOptions(0L, false, false),
            registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
            mc.gui.screen());
        *///?} else if >=1.21.11 {
        /*LevelSettings settings = new LevelSettings("smoke", type, false, Difficulty.PEACEFUL, true,
            new GameRules(WorldDataConfiguration.DEFAULT.enabledFeatures()), WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel("smoke", settings, new WorldOptions(0L, false, false),
            registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
            mc.screen);
        *///?} else {
        LevelSettings settings = new LevelSettings("smoke", type, false, Difficulty.PEACEFUL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel("smoke", settings, new WorldOptions(0L, false, false),
            registries -> registries.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
            mc.screen);
        //?}
    }

    /** Saves a screenshot into screenshots/ (on 26.x under the game's own timestamped name). */
    public static void screenshot(Minecraft mc, String name) {
        //? if >=26 {
        /*Screenshot.grab(mc, false);
        *///?} else if >=1.21.11 {
        /*Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(), 1, msg -> {});
        *///?} else {
        Screenshot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(), msg -> {});
        //?}
    }
}

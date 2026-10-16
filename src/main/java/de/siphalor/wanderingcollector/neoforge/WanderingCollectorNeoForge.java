package de.siphalor.wanderingcollector.neoforge;

//? if neoforge {
import de.siphalor.wanderingcollector.WCConfig;
import de.siphalor.wanderingcollector.WanderingCollector;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod(WanderingCollector.MOD_ID)
public class WanderingCollectorNeoForge {
	public WanderingCollectorNeoForge() {
		WCConfig.load(FMLPaths.CONFIGDIR.get());
	}
}
//?}

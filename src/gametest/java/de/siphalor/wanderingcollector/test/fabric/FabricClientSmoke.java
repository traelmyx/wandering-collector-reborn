package de.siphalor.wanderingcollector.test.fabric;

//? if fabric {
/*import de.siphalor.wanderingcollector.test.ClientSmoke;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FabricClientSmoke implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (ClientSmoke.start()) ClientTickEvents.END_CLIENT_TICK.register(ClientSmoke::tick);
	}
}
*///?}

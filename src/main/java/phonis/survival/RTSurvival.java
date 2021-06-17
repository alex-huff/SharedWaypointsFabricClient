package phonis.survival;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import phonis.survival.networking.RTSurvivalReceiver;

public class RTSurvival implements ModInitializer {

	@Override
	public void onInitialize() {
		System.out.println("Hello Fabric world!");
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("rtsurvival:main"), new RTSurvivalReceiver());
	}

}

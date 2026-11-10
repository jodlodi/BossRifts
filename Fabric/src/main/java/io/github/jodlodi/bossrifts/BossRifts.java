package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.platform.FabricConfigHelper;
import io.github.jodlodi.bossrifts.platform.FabricNetworkHelper;
import net.fabricmc.api.ModInitializer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BossRifts implements ModInitializer {

	@Override
	public void onInitialize() {
		CommonClass.init();
		FabricNetworkHelper.init();
		FabricConfigHelper.getHelper().generateDefaultConfig();
	}
}

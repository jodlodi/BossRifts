package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.platform.ForgeConfigHelper;
import io.github.jodlodi.bossrifts.platform.ForgeNetworkHelper;
import io.github.jodlodi.bossrifts.platform.ForgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod(Constants.MOD_ID)
public class BossRifts {

	public BossRifts(IEventBus bus, ModContainer container) {
		CommonClass.init();
		NeoForge.EVENT_BUS.register(DeathListener.class);
		NeoForge.EVENT_BUS.register(PearlListener.class);

		ForgeRegistryHelper.DATA_COMPONENTS.register(bus);
		ForgeRegistryHelper.BLOCK_ENTITIES.register(bus);
		ForgeRegistryHelper.PARTICLE_TYPES.register(bus);
		ForgeRegistryHelper.ENTITY_TYPES.register(bus);
		ForgeRegistryHelper.SOUND_TYPES.register(bus);
		ForgeRegistryHelper.BLOCKS.register(bus);
		ForgeRegistryHelper.ITEMS.register(bus);

		container.registerConfig(ModConfig.Type.SERVER, ForgeConfigHelper.SPEC);
	}
}

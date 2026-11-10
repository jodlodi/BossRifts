package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.platform.NeoForgeConfigHelper;
import io.github.jodlodi.bossrifts.platform.NeoForgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod(Constants.MOD_ID)
public class BossRifts {

	public BossRifts(IEventBus bus, ModContainer container) {
		CommonClass.init();
		NeoForge.EVENT_BUS.register(DeathListener.class);
		NeoForge.EVENT_BUS.register(PearlListener.class);

		NeoForgeRegistryHelper.DATA_COMPONENTS.register(bus);
		NeoForgeRegistryHelper.BLOCK_ENTITIES.register(bus);
		NeoForgeRegistryHelper.PARTICLE_TYPES.register(bus);
		NeoForgeRegistryHelper.ENTITY_TYPES.register(bus);
		NeoForgeRegistryHelper.SOUND_TYPES.register(bus);
		NeoForgeRegistryHelper.BLOCKS.register(bus);
		NeoForgeRegistryHelper.ITEMS.register(bus);

		container.registerConfig(ModConfig.Type.SERVER, NeoForgeConfigHelper.SPEC);
	}
}

package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.platform.ForgeConfigHelper;
import io.github.jodlodi.bossrifts.platform.ForgeNetworkHelper;
import io.github.jodlodi.bossrifts.platform.ForgeRegistryHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BossRifts {

	public BossRifts(FMLJavaModLoadingContext context) {
		CommonClass.init();
		IEventBus bus = context.getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(DeathListener.class);
		MinecraftForge.EVENT_BUS.register(PearlListener.class);

		ForgeRegistryHelper.BLOCK_ENTITIES.register(bus);
		ForgeRegistryHelper.PARTICLE_TYPES.register(bus);
		ForgeRegistryHelper.ENTITY_TYPES.register(bus);
		ForgeRegistryHelper.SOUND_TYPES.register(bus);
		ForgeRegistryHelper.BLOCKS.register(bus);
		ForgeRegistryHelper.ITEMS.register(bus);

		context.registerConfig(ModConfig.Type.SERVER, ForgeConfigHelper.SPEC);
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent evt) {
		ForgeNetworkHelper.init();
	}
}

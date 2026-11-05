package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.platform.ForgeNetworkHelper;
import io.github.jodlodi.bossrifts.platform.ForgeRegistryHelper;
import io.github.jodlodi.bossrifts.platform.ForgeConfigHelper;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BossRifts {
	public static final Tags.IOptionalNamedTag<EntityType<?>> LEGACY_BOSSES = EntityTypeTags.createOptional(Constants.prefix("legacy_bosses"));
	public static final Tags.IOptionalNamedTag<EntityType<?>> RIFT_BOSSES = EntityTypeTags.createOptional(Constants.prefix("rift_bosses"));

	public BossRifts() {
		CommonClass.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(DeathListener.class);
		MinecraftForge.EVENT_BUS.register(PearlListener.class);

		ForgeRegistryHelper.BLOCK_ENTITIES.register(bus);
		ForgeRegistryHelper.PARTICLE_TYPES.register(bus);
		ForgeRegistryHelper.ENTITY_TYPES.register(bus);
		ForgeRegistryHelper.SOUND_TYPES.register(bus);
		ForgeRegistryHelper.BLOCKS.register(bus);
		ForgeRegistryHelper.ITEMS.register(bus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfigHelper.SPEC);
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent evt) {
		ForgeNetworkHelper.init();
	}
}

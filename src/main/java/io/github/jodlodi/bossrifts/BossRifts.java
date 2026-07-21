package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.ClientSetupListener;
import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BossRifts.MOD_ID)
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class BossRifts {
	public static final String MOD_ID = "bossrifts";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Tags.IOptionalNamedTag<EntityType<?>> RIFT_BOSSES = EntityTypeTags.createOptional(riftResource("rift_bosses"));

	public BossRifts() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);

		MinecraftForge.EVENT_BUS.register(DeathListener.class);
		MinecraftForge.EVENT_BUS.register(PearlListener.class);
		MinecraftForge.EVENT_BUS.register(ClientSetupListener.class);

		ModEntities.ENTITY_TYPES.register(bus);
		ModSounds.SOUND_TYPES.register(bus);
		ModItems.ITEMS.register(bus);

		bus.addListener(this::configSetup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RiftConfig.SPEC);
	}

	public static ResourceLocation riftResource(String key) {
		return new ResourceLocation(MOD_ID, key);
	}

	private void configSetup(final FMLCommonSetupEvent event) {
		RiftConfig.refresh();
	}
}

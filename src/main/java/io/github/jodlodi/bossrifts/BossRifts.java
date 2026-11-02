package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.network.ModPacketHandler;
import io.github.jodlodi.bossrifts.registry.*;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(BossRifts.MOD_ID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BossRifts {
	public static final String MOD_ID = "bossrifts";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Tags.IOptionalNamedTag<EntityType<?>> LEGACY_BOSSES = EntityTypeTags.createOptional(prefix("legacy_bosses"));
	public static final Tags.IOptionalNamedTag<EntityType<?>> RIFT_BOSSES = EntityTypeTags.createOptional(prefix("rift_bosses"));

	public BossRifts() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(DeathListener.class);
		MinecraftForge.EVENT_BUS.register(PearlListener.class);

		ModBlockEntities.BLOCK_ENTITIES.register(bus);
		ModParticles.PARTICLE_TYPES.register(bus);
		ModEntities.ENTITY_TYPES.register(bus);
		ModSounds.SOUND_TYPES.register(bus);
		ModBlocks.BLOCKS.register(bus);
		ModItems.ITEMS.register(bus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RiftConfig.SPEC);
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent evt) {
		ModPacketHandler.init();
	}

	public static ResourceLocation prefix(String key) {
		return new ResourceLocation(MOD_ID, key);
	}
}

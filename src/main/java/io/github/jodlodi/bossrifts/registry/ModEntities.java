package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.entities.RiftPearlEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, BossRifts.MOD_ID);

	public static final RegistryObject<EntityType<BossRiftEntity>> BOSS_RIFT = ENTITY_TYPES.register("boss_rift",
			() -> EntityType.Builder.<BossRiftEntity>of(BossRiftEntity::new, EntityClassification.MISC)
					.setCustomClientFactory(BossRiftEntity::new)
					.updateInterval(1)
					.sized(0.5F, 0.5F)
					.build("boss_rift"));

	public static final RegistryObject<EntityType<RiftPearlEntity>> RIFT_PEARL = ENTITY_TYPES.register("rift_pearl",
			() -> EntityType.Builder.<RiftPearlEntity>of(RiftPearlEntity::new, EntityClassification.MISC)
					.setCustomClientFactory(RiftPearlEntity::new)
					.updateInterval(4)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4)
					.build("rift_pearl"));
}
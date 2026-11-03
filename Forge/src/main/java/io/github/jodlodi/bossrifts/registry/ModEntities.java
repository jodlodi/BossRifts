package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, BossRifts.MOD_ID);

	public static final RegistryObject<EntityType<BossRiftEntity>> BOSS_RIFT = ENTITY_TYPES.register("boss_rift",
			() -> EntityType.Builder.<BossRiftEntity>of(BossRiftEntity::new, MobCategory.MISC)
					.setCustomClientFactory(BossRiftEntity::new)
					.updateInterval(1)
					.sized(0.5F, 0.5F)
					.build("boss_rift"));
}
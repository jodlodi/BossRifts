package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModEntities {
	public static final MultiSupplier<EntityType<BossRiftEntity>> BOSS_RIFT = Services.REGISTRY.registerEntityType("boss_rift",
			() -> EntityType.Builder.of(BossRiftEntity::new, MobCategory.MISC)
					.updateInterval(1)
					.sized(0.5F, 0.5F)
					.build("boss_rift"));

	public static void init() {

	}
}
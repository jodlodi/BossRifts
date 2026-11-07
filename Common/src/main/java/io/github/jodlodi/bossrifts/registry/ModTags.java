package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModTags {
	public static final TagKey<EntityType<?>> LEGACY_BOSSES = TagKey.create(Registries.ENTITY_TYPE, Constants.prefix("legacy_bosses"));
	public static final TagKey<EntityType<?>> RIFT_BOSSES = TagKey.create(Registries.ENTITY_TYPE, Constants.prefix("rift_bosses"));
}

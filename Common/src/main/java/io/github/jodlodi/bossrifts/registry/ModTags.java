package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.Constants;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModTags {
	public static final TagKey<EntityType<?>> RIFT_BOSSES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Constants.prefix("rift_bosses"));
}

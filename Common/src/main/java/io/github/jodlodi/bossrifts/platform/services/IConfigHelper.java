package io.github.jodlodi.bossrifts.platform.services;

import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IConfigHelper {

	boolean isV1();

	boolean isExpired(long time);

	int empowerDuration();

	boolean isOneTimeUse();

	double getExpCost();

	double getRange();

	Tag<EntityType<?>> getTag();

	boolean canSetSpawn();
}

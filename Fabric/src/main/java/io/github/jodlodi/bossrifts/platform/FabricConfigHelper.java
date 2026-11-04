package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.platform.services.IConfigHelper;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricConfigHelper implements IConfigHelper {

	@Override
	public boolean isV1() {
		return false;
	}

	@Override
	public boolean isExpired(long time) {
		return false;
	}

	@Override
	public int empowerDuration() {
		return 100;
	}

	@Override
	public boolean isOneTimeUse() {
		return false;
	}

	@Override
	public double getExpCost() {
		return 1.0D;
	}

	@Override
	public double getRange() {
		return 5.0D;
	}

	@Override
	public Tag<EntityType<?>> getTag() {
		return EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS;
	}

	@Override
	public boolean canSetSpawn() {
		return true;
	}
}

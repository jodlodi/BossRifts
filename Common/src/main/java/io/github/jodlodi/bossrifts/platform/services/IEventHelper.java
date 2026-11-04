package io.github.jodlodi.bossrifts.platform.services;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface IEventHelper {

	@Nullable
	Vec3 onTeleport(Entity entity, Vec3 destination);
}
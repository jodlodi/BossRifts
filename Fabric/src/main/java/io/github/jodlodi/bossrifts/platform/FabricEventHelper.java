package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FabricEventHelper implements IEventHelper {

    @Override
    public Vec3 onTeleport(Entity entity, Vec3 destination) {
        return destination;
    }
}
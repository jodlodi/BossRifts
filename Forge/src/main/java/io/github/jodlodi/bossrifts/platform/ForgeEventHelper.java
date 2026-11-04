package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ForgeEventHelper implements IEventHelper {

    @Nullable
    @Override
    public Vec3 onTeleport(Entity entity, Vec3 destination) {
        EntityTeleportEvent.TeleportCommand event = ForgeEventFactory.onEntityTeleportCommand(entity, destination.x, destination.y, destination.z);
        if (event.isCanceled()) return null;
        return new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }
}
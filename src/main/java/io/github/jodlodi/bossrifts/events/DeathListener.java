package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.registry.Reg;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class DeathListener {
    @SubscribeEvent
    @ParametersAreNonnullByDefault
    public static void livingEntityDeath(LivingDeathEvent event) {
        LivingEntity dyingEntity = event.getEntityLiving();
        if (!dyingEntity.level.isClientSide() && dyingEntity.getType().is(Reg.RIFT_BOSSES)) {

            if (!dyingEntity.level.getEntities(dyingEntity, dyingEntity.getBoundingBox().inflate(32),
                    entity -> entity.getType().is(Reg.RIFT_BOSSES) && entity instanceof LivingEntity living && !living.isDeadOrDying()).isEmpty()) return;

            BossRiftEntity bossRift = Reg.BOSS_RIFT.get().create(dyingEntity.level);
            if (bossRift != null) {
                BlockPos pos = dyingEntity.blockPosition().above();
                if (dyingEntity.level.getBlockState(pos).isCollisionShapeFullBlock(dyingEntity.level, pos)) {
                    dyingEntity.level.removeBlock(pos, false);
                }
                bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
                dyingEntity.level.addFreshEntity(bossRift);
            }
        }
    }
}
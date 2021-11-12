package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class DeathListener {
    @SubscribeEvent
    public static void livingEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getType().is(Reg.RIFT_BOSSES)) {
            BossRiftEntity bossRift = Reg.BOSS_RIFT.get().create(entity.level);
            if (bossRift != null) {
                BlockPos pos = entity.blockPosition().above();
                if (entity.level.getBlockState(pos).isCollisionShapeFullBlock(entity.level, pos)) {
                    entity.level.removeBlock(pos, false);
                }
                bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
                entity.level.addFreshEntity(bossRift);
                for (int i = 0; i < 30; i++) {
                    double d0 = pos.getX() + 0.25D + entity.getRandom().nextDouble() / 2;
                    double d1 = pos.getY() + 1.5D;
                    double d2 = pos.getZ() + 0.25D + entity.getRandom().nextDouble() / 2;
                    entity.level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}

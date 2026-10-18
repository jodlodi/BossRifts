package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class DeathListener {
	@SubscribeEvent
	public static void livingEntityDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity.getType().is(BossRifts.RIFT_BOSSES) && !entity.level.isClientSide()) {
			DamageSource source = event.getSource();

			List<PlayerEntity> responsible = new ArrayList<>();

			if (source.getDirectEntity() instanceof PlayerEntity) responsible.add((PlayerEntity) source.getDirectEntity());
			else if (source.getEntity() instanceof PlayerEntity) responsible.add((PlayerEntity) source.getEntity());

			for (Entity in : entity.level.getEntities(entity, entity.getBoundingBox().inflate(32), EntityPredicates.ENTITY_STILL_ALIVE)) {
				if (in instanceof PlayerEntity) responsible.add((PlayerEntity) in);
			}

			for (PlayerEntity player : responsible) {
				findAndEmpowerPearl(player);
			}

			if (true) return;

			BossRiftEntity bossRift = ModEntities.BOSS_RIFT.get().create(entity.level);
			if (bossRift != null) {
				BlockPos pos = entity.blockPosition().above();
				if (entity.level.getBlockState(pos).isCollisionShapeFullBlock(entity.level, pos)) {
					entity.level.removeBlock(pos, false);
				}
				bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
				entity.level.addFreshEntity(bossRift);
			}
		}
	}

	public static void findAndEmpowerPearl(PlayerEntity player) {
		long time = player.level.getGameTime();
		if (empowerPearl(player.getMainHandItem(), time)) return;
		if (empowerPearl(player.getOffhandItem(), time)) return;

		for (ItemStack stack : player.inventory.items) {
			if (empowerPearl(stack, time)) return;
		}
	}

	public static boolean empowerPearl(ItemStack stack, long time) {
		if (stack.getItem() instanceof RiftPearlItem) {
			stack.getOrCreateTag().putLong(RiftPearlItem.POWERED, time);
			return true;
		}
		return false;
	}
}

package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
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
		LivingEntity dying = event.getEntityLiving();
		if (dying.getType().is(BossRifts.RIFT_BOSSES) && !dying.level.isClientSide()) {
			DamageSource source = event.getSource();
			if (!RiftConfig.isV1()) triggerV2(dying, source);
			else triggerV1(dying);
		}
	}

	public static void triggerV2(LivingEntity dying, DamageSource source) {
		List<PlayerEntity> responsible = new ArrayList<>();

		if (source.getDirectEntity() instanceof PlayerEntity) responsible.add((PlayerEntity) source.getDirectEntity());
		else if (source.getEntity() instanceof PlayerEntity) responsible.add((PlayerEntity) source.getEntity());

		for (Entity in : dying.level.getEntities(dying, dying.getBoundingBox().inflate(32), EntityPredicates.ENTITY_STILL_ALIVE)) {
			if (in instanceof PlayerEntity) responsible.add((PlayerEntity) in);
		}

		for (PlayerEntity player : responsible) findAndEmpowerPearl(player);
	}

	public static void triggerV1(LivingEntity dying) {
		if (!dying.level.getEntities(dying, dying.getBoundingBox().inflate(32), entity -> BossRifts.RIFT_BOSSES.contains(entity.getType())).isEmpty())
			return;

		BossRiftEntity bossRift = ModEntities.BOSS_RIFT.get().create(dying.level);
		if (bossRift != null) {
			ItemStack stack = bossRift.getItem();
			RiftPearlItem.empowerPearl(stack, dying.level.getGameTime());
			bossRift.setItem(stack);

			BlockPos pos = dying.blockPosition().above();
			if (dying.level.getBlockState(pos).isCollisionShapeFullBlock(dying.level, pos)) {
				dying.level.removeBlock(pos, false);
			}
			bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
			dying.level.addFreshEntity(bossRift);
		}
	}

	public static void findAndEmpowerPearl(PlayerEntity player) {
		long time = player.level.getGameTime();
		if (RiftPearlItem.empowerPearl(player.getMainHandItem(), time)) return;
		if (RiftPearlItem.empowerPearl(player.getOffhandItem(), time)) return;

		for (ItemStack stack : player.inventory.items) {
			if (RiftPearlItem.empowerPearl(stack, time)) return;
		}
	}
}

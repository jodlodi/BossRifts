package io.github.jodlodi.bossrifts.platform.services;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

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

	static void triggerV1(LivingEntity dying) {
		if (!dying.level.getEntities(dying, dying.getBoundingBox().inflate(32), entity -> Services.CONFIG.getTag().contains(entity.getType())).isEmpty()) {
			return;
		}

		BossRiftEntity bossRift = ModEntities.BOSS_RIFT.get().create(dying.level);
		if (bossRift != null) {
			BlockPos pos = dying.blockPosition().above();
			if (dying.level.getBlockState(pos).isCollisionShapeFullBlock(dying.level, pos)) {
				dying.level.removeBlock(pos, false);
			}
			bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
			dying.level.addFreshEntity(bossRift);
		}
	}

	static void triggerV2(LivingEntity dying, DamageSource source) {
		if (Services.CONFIG.getExpCost() >= 0.0F) return;

		List<Player> responsible = new ArrayList<>();

		if (source.getDirectEntity() instanceof Player player) responsible.add(player);
		else if (source.getEntity() instanceof Player player) responsible.add(player);

		for (Entity in : dying.level.getEntities(dying, dying.getBoundingBox().inflate(32), Entity::isAlive)) {
			if (in instanceof Player player) responsible.add(player);
		}

		for (Player player : responsible) findAndEmpowerPearl(player);
	}

	static void findAndEmpowerPearl(Player player) {
		long time = player.level.getGameTime();
		if (RiftPearlItem.empowerPearl(player.getMainHandItem(), time)) return;
		if (RiftPearlItem.empowerPearl(player.getOffhandItem(), time)) return;

		for (ItemStack stack : player.getInventory().items) {
			if (RiftPearlItem.empowerPearl(stack, time)) return;
		}
	}
}

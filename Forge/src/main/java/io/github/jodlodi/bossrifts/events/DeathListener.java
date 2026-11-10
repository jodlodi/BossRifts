package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.platform.services.IConfigHelper;
import io.github.jodlodi.bossrifts.registry.ModTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class DeathListener {
	@SubscribeEvent
	public static void onLivingEntityDeath(LivingDeathEvent event) {
		LivingEntity dying = event.getEntity();
		if (dying.getType().is(ModTags.RIFT_BOSSES) && !dying.level().isClientSide()) {
			DamageSource source = event.getSource();
			if (!Services.CONFIG.isV1()) IConfigHelper.triggerV2(dying, source);
			else IConfigHelper.triggerV1(dying);
		}
	}
}

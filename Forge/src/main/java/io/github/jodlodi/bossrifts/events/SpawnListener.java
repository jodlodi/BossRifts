package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class SpawnListener {
	@SubscribeEvent
	public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof BossRiftEntity bossRift) bossRift.spawn();
	}
}

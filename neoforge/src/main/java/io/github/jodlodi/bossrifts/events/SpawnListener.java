package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
@EventBusSubscriber(modid = Constants.MOD_ID)
public class SpawnListener {

	@SubscribeEvent
	public static void onEntityJoinLevelEvent(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof BossRiftEntity bossRift) bossRift.spawn();
	}
}

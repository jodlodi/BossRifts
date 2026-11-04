package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModSounds {
	public static final MultiSupplier<SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
	public static final MultiSupplier<SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
	public static final MultiSupplier<SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
	public static final MultiSupplier<SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
	public static final MultiSupplier<SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
	public static final MultiSupplier<SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");

	public static final MultiSupplier<SoundEvent> RIFT_AGGREGATOR_AMBIENT = riftSound("block.boss_rift.rift_aggregator.ambient");
	public static final MultiSupplier<SoundEvent> RIFT_AGGREGATOR_SET_SPAWN = riftSound("block.boss_rift.rift_aggregator.set_spawn");

	private static MultiSupplier<SoundEvent> riftSound(String key) {
		return Services.REGISTRY.registerSound(key, () -> new SoundEvent(Constants.prefix(key)));
	}

	public static void init() {

	}
}

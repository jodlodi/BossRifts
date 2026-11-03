package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModSounds {
	public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BossRifts.MOD_ID);
	public static final RegistryObject<SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
	public static final RegistryObject<SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
	public static final RegistryObject<SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
	public static final RegistryObject<SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
	public static final RegistryObject<SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
	public static final RegistryObject<SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");

	public static final RegistryObject<SoundEvent> RIFT_AGGREGATOR_AMBIENT = riftSound("block.boss_rift.rift_aggregator.ambient");
	public static final RegistryObject<SoundEvent> RIFT_AGGREGATOR_SET_SPAWN = riftSound("block.boss_rift.rift_aggregator.set_spawn");

	private static RegistryObject<SoundEvent> riftSound(String key) {
		return SOUND_TYPES.register(key, () -> new SoundEvent(BossRifts.prefix(key)));
	}
}

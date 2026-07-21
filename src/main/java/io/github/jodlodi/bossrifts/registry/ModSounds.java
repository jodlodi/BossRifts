package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModSounds {
	public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BossRifts.MOD_ID);
	public static final RegistryObject<SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
	public static final RegistryObject<SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
	public static final RegistryObject<SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
	public static final RegistryObject<SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
	public static final RegistryObject<SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
	public static final RegistryObject<SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");

	private static RegistryObject<SoundEvent> riftSound(String key) {
		return SOUND_TYPES.register(key, () -> new SoundEvent(BossRifts.riftResource(key)));
	}
}

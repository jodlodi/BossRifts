package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.CommonListener;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BossRifts.MOD_ID)
public class BossRifts
{
    public static final String MOD_ID = "bossrifts";

    public BossRifts(IEventBus bus) {
        NeoForge.EVENT_BUS.register(CommonListener.class);

        Reg.ENTITY_TYPES.register(bus);
        Reg.SOUND_TYPES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RiftConfig.SPEC);
    }
}
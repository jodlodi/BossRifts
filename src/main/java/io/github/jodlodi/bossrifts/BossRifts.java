package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.events.ClientSetupListener;
import io.github.jodlodi.bossrifts.events.DeathListener;
import io.github.jodlodi.bossrifts.events.PearlListener;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BossRifts.MOD_ID)
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class BossRifts
{
    public static final String MOD_ID = "bossrifts";

    public BossRifts() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(DeathListener.class);
        MinecraftForge.EVENT_BUS.register(PearlListener.class);
        MinecraftForge.EVENT_BUS.register(ClientSetupListener.class);

        Reg.ENTITY_TYPES.register(bus);
        Reg.SOUND_TYPES.register(bus);

        bus.addListener(this::configSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, RiftConfig.SPEC);
    }

    private void configSetup(final FMLCommonSetupEvent event) {
        RiftConfig.refresh();
    }
}
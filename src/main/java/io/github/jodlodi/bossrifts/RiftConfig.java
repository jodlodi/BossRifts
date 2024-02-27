package io.github.jodlodi.bossrifts;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RiftConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue EXPIRE_SPAN = BUILDER
            .comment("How long should the Rift take to naturally expire? (20 ticks is 1 second)",
                    "If you type in the number 0, the Rift will never naturally expire.")
            .defineInRange("How many ticks to Rifts last?: ", 2400, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue REUSABLE_STATE = BUILDER
            .comment("Should the Rift disappear after being used once? (Using an Ender Pearl to use a rift lets you bypass this)")
            .define("Should the Rift disappear on use?: ", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean expireState;
    public static int expireSpan;
    public static boolean reusableState;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        expireSpan = EXPIRE_SPAN.get();
        expireState = expireSpan != 0;
        reusableState = REUSABLE_STATE.get();
    }
}
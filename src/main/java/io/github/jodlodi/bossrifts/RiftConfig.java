package io.github.jodlodi.bossrifts;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class RiftConfig {
    public static final Server CONFIG;
    public static final ForgeConfigSpec SPEC;

    static{
        final Pair<Server, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Server::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    public static boolean expireState;
    public static int expireSpan;
    public static boolean reusableState;

    public static class Server {
        public final ConfigValue<Integer> finalExpireSpan;
        public final ConfigValue<Boolean> finalReusableState;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("How long should the Rift take to naturally expire? (20 ticks is 1 second)" +
                    "\nIf you type in the number 0, the Rift will never naturally expire.");
            finalExpireSpan = builder.define("How many ticks to Rifts last?: ", 2400);
            builder.comment("\nShould the Rift disappear after being used once? (Using an Ender Pearl to use a rift lets you bypass this)");
            finalReusableState = builder.define("Should the Rift disappear on use?: ", false);
        }
    }

    public static void refresh() {
        expireSpan = CONFIG.finalExpireSpan.get();
        expireState = expireSpan != 0;
        reusableState = CONFIG.finalReusableState.get();
    }
}
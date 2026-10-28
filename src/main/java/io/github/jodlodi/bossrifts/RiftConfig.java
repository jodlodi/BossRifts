package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.iRift;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class RiftConfig {
	public static final Server CONFIG;
	public static final ForgeConfigSpec SPEC;

	static {
		final Pair<Server, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Server::new);
		CONFIG = pair.getLeft();
		SPEC = pair.getRight();
	}

	public static class Server {
		public final ConfigValue<Boolean> legacyModeConfig;
		public final ConfigValue<Integer> expireSpanConfig;
		public final ConfigValue<Boolean> oneTimeUse;
		public final ConfigValue<Double> expCost;

		private Server(ForgeConfigSpec.Builder builder) {
			builder.comment("Should the mod behave like it did in 1.0? In which Rifts would automatically appear when a boss is killed instead of being items?" +
					"\nThe Rift Pearl item serves no purpose on 1.0. It's up to the modpack maker to either give it one or use a mod to remove it.");
			this.legacyModeConfig = builder.define("Enable legacy behaviour?: ", false);

			builder.comment("\nHow long should a Rift remain charged for? (20 ticks is 1 second)" +
					"\nIf you type in the number -1, the Rift will never naturally expire.");
			this.expireSpanConfig = builder.define("How many ticks to Rifts last?: ", AnimUtil.minutesToTicks(5));

			builder.comment("\nShould the Rift disappear after being used once? (Using an Ender Pearl to use a rift lets you bypass this)");
			this.oneTimeUse = builder.define("Do Rift pearls disappear upon use? : ", true);

			builder.comment("\nShould using activating a Rift Pearl cost exp instead of killing a boss?");
			this.expCost = builder.define("Exp cost per chunk of distance, negative value disables this in favour of default behaviour: ", -1.0D);
		}
	}

	public static boolean isV1() {
		return CONFIG.legacyModeConfig.get();
	}

	public static boolean isExpired(long time) {
		int span = CONFIG.expireSpanConfig.get();
		if (span < 0) return false;
		return empowerDuration() < time;
	}

	public static int empowerDuration() {
		return Math.max(CONFIG.expireSpanConfig.get(), 0) + iRift.TRANSITION_TIME;
	}

	public static boolean isOneTimeUse() {
		return CONFIG.oneTimeUse.get();
	}

	public static double getExpCost() {
		return CONFIG.expCost.get();
	}
}

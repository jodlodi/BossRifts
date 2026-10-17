package io.github.jodlodi.bossrifts;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class RiftCubeSolver {
	private static final int[][] NORMALS = {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
	private static final int CACHE_SIZE = 256;
	private static final int SHUFFLES = 24;
	private static final float SPEED_UP = 0.95F;

	private static final Map<Long, Plan> CACHE = new LinkedHashMap<Long, Plan>(64, 0.75F, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Plan> eldest) {
			return this.size() > CACHE_SIZE;
		}
	};

	public static final class Plan {
		private final float[] starts; // starts[i] = timeline fraction where move i begins, starts[size] = 1.0

		private final int[] moves;

		private Plan(int[] moves) {
			this.moves = moves;

			this.starts = new float[moves.length + 1];

			float total = 0.0F;
			float weight = 1.0F;
			for (int i = 0; i < moves.length; i++) {
				total += weight;
				weight *= SPEED_UP;
			}

			float at = 0.0F;
			weight = 1.0F;
			for (int i = 0; i < moves.length; i++) {
				this.starts[i] = at;
				at += weight / total;
				weight *= SPEED_UP;
			}
			this.starts[moves.length] = 1.0F;
		}

		public float start(int index) {
			return this.starts[index];
		}

		public float end(int index) {
			return this.starts[index + 1];
		}

		public int movesStarted(float timeScale) {
			int n = 0;
			while (n < this.moves.length && timeScale >= this.starts[n]) n++;
			return n;
		}

		public int size() {
			return this.moves.length;
		}

		public int face(int index) {
			return this.moves[index] / 3;
		}

		public float degrees(int index) {
			int quarters = this.moves[index] % 3 + 1;
			return quarters == 3 ? -90.0F : quarters * 90.0F;
		}
	}

	public static int[] normal(int face) {
		return NORMALS[face];
	}

	public static synchronized Plan plan(long seed) {
		Plan plan = CACHE.get(seed);
		if (plan == null) {
			plan = compute(seed);
			CACHE.put(seed, plan);
		}
		return plan;
	}

	private static Plan compute(long seed) {
		Random random = new Random(seed);
		int[] moves = new int[SHUFFLES];
		int lastAxis = -1;
		for (int i = 0; i < SHUFFLES; i++) {
			int face;
			do face = random.nextInt(6); while (face / 2 == lastAxis);
			lastAxis = face / 2;
			moves[i] = face * 3 + random.nextInt(3);
		}
		return new Plan(moves);
	}
}
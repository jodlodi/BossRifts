package io.github.jodlodi.bossrifts.client.rift;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BossRiftRenderState extends EntityRenderState {
	public long seed;
	public long time;
	public long power;
	public float points;
	public float points0;
	public float partialTick;
	public boolean foil;
}
package io.github.jodlodi.bossrifts.util;

import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
public class MultiSupplier<R> implements Supplier<R> {
	private final Supplier<R> supplier;
	private final ResourceLocation id;

	public MultiSupplier(Supplier<R> supplier, ResourceLocation id) {
		this.supplier = supplier;
		this.id = id;
	}

	@Override
	public R get() {
		return this.supplier.get();
	}

	public ResourceLocation id() {
		return this.id;
	}
}

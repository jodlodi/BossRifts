package io.github.jodlodi.bossrifts.util;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
public class MultiSupplier<R> implements Supplier<R> {
	private final Supplier<R> supplier;
	private final Identifier id;

	public MultiSupplier(Supplier<R> supplier, Identifier id) {
		this.supplier = supplier;
		this.id = id;
	}

	@Override
	public R get() {
		return this.supplier.get();
	}

	public Identifier id() {
		return this.id;
	}
}

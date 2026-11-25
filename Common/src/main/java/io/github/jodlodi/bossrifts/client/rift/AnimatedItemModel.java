package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.Constants;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AnimatedItemModel(ItemModel.Unbaked wrapped) implements ItemModel.Unbaked {
	public static final Identifier ID = Constants.prefix("animated");

	public static final MapCodec<AnimatedItemModel> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			ItemModels.CODEC.fieldOf("model").forGetter(AnimatedItemModel::wrapped)
	).apply(i, AnimatedItemModel::new));

	@Override
	public MapCodec<AnimatedItemModel> type() {
		return MAP_CODEC;
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver) {
		this.wrapped.resolveDependencies(resolver);
	}

	@Override
	public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
		ItemModel baked = this.wrapped.bake(context, transformation);
		return (output, item, resolver, displayContext, level, owner, seed) -> {
			baked.update(output, item, resolver, displayContext, level, owner, seed);
			output.setAnimated();
		};
	}
}
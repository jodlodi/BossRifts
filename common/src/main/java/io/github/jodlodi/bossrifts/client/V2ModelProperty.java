package io.github.jodlodi.bossrifts.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record V2ModelProperty() implements SelectItemModelProperty<Boolean> {
	public static final Codec<Boolean> VALUE_CODEC = Codec.BOOL;
	public static final SelectItemModelProperty.Type<V2ModelProperty, Boolean> TYPE = Type.create(MapCodec.unit(new V2ModelProperty()), VALUE_CODEC);

	@Override
	public Boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
		return !Services.CONFIG.isV1();
	}

	@Override
	public Codec<Boolean> valueCodec() {
		return VALUE_CODEC;
	}

	@Override
	public Type<V2ModelProperty, Boolean> type() {
		return TYPE;
	}
}

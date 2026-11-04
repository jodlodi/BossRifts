package io.github.jodlodi.bossrifts.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

@NullMarked
@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityAccessor {
	@Invoker("isOnCooldown")
	boolean invokeIsOnCooldown();

	@Invoker("setCooldown")
	void invokeSetCooldown(int cooldown);

	@Invoker("tryMoveItems")
	static boolean invokeTryMoveItems(Level level, BlockPos pos, BlockState state, HopperBlockEntity entity, BooleanSupplier supplier) {
		throw new RuntimeException();
	}

	@Invoker("isEmptyContainer")
	static boolean invokeIsEmptyContainer(Container container, Direction direction) {
		throw new RuntimeException();
	}

	@Invoker("getSlots")
	static IntStream invokeGetSlots(Container container, Direction direction) {
		throw new RuntimeException();
	}

	@Invoker("tryTakeInItemFromSlot")
	static boolean invokeTryTakeInItemFromSlot(Hopper hopper, Container container, int i, Direction direction) {
		throw new RuntimeException();
	}
}

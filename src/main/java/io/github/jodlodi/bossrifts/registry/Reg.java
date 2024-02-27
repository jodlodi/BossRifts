package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Reg {
    public static final TagKey<EntityType<?>> BOSS_EXCEPTION = TagKey.create(Registries.ENTITY_TYPE, riftResource("boss_exception"));

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, BossRifts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(Registries.SOUND_EVENT, BossRifts.MOD_ID);

    @Nonnull
    public static ResourceLocation riftResource(String key) {
        return new ResourceLocation(BossRifts.MOD_ID, key);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> riftSound(String key) {
        return SOUND_TYPES.register(key, () -> SoundEvent.createVariableRangeEvent(riftResource(key)));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<BossRiftEntity>> BOSS_RIFT = ENTITY_TYPES.register("boss_rift",
            () -> EntityType.Builder.<BossRiftEntity>of(BossRiftEntity::new, MobCategory.MISC)
                    .sized(0.5F,0.5F)
                    .build(riftResource("boss_rift").toString()));

    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");
}
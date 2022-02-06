package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import io.github.jodlodi.bossrifts.BossRifts;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class Reg {
    public static final Tags.IOptionalNamedTag<EntityType<?>> RIFT_BOSSES = EntityTypeTags.createOptional(riftResource("rift_bosses"));

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, BossRifts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BossRifts.MOD_ID);

    @Nonnull
    public static ResourceLocation riftResource(String key) {
        return new ResourceLocation(BossRifts.MOD_ID, key);
    }

    private static RegistryObject<SoundEvent> riftSound(String key) {
        return SOUND_TYPES.register(key, () -> new SoundEvent(riftResource(key)));
    }

    public static final RegistryObject<EntityType<BossRiftEntity>> BOSS_RIFT = ENTITY_TYPES.register("boss_rift",
            () -> EntityType.Builder.<BossRiftEntity>of(BossRiftEntity::new, EntityClassification.MISC)
                    .setCustomClientFactory(BossRiftEntity::new)
                    .sized(0.5F,0.5F)
                    .build("boss_rift"));

    public static final RegistryObject<SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
    public static final RegistryObject<SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
    public static final RegistryObject<SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
    public static final RegistryObject<SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
    public static final RegistryObject<SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
    public static final RegistryObject<SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");
}
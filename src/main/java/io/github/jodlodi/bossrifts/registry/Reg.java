package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.RiftRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Reg {
    public static final IOptionalNamedTag<EntityType<?>> RIFT_BOSSES = EntityTypeTags.createOptional(riftResource("rift_bosses"));

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, BossRifts.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BossRifts.MOD_ID);

    public static ResourceLocation riftResource(String key) {
        return new ResourceLocation(BossRifts.MOD_ID, key);
    }

    private static RegistryObject<SoundEvent> riftSound(String key) {
        return SOUND_TYPES.register(key, () -> new SoundEvent(riftResource(key)));
    }

    public static final RegistryObject<EntityType<BossRiftEntity>> BOSS_RIFT = ENTITY_TYPES.register("boss_rift",
            () -> EntityType.Builder.<BossRiftEntity>of(BossRiftEntity::new, MobCategory.MISC)
                    .sized(0.5F,0.5F)
                    .setCustomClientFactory(BossRiftEntity::new)
                    .build(riftResource("boss_rift").toString()));

    public static final RegistryObject<SoundEvent> RIFT_SPAWN = riftSound("entity.boss_rift.spawn");
    public static final RegistryObject<SoundEvent> RIFT_OPEN = riftSound("entity.boss_rift.open");
    public static final RegistryObject<SoundEvent> RIFT_CLOSE = riftSound("entity.boss_rift.close");
    public static final RegistryObject<SoundEvent> RIFT_REV_UP = riftSound("entity.boss_rift.rev_up");
    public static final RegistryObject<SoundEvent> RIFT_WARP = riftSound("entity.boss_rift.warp");
    public static final RegistryObject<SoundEvent> RIFT_EXPIRE = riftSound("entity.boss_rift.expire");

    public static final ModelLayerLocation BOSS_RIFT_MODEL =  new ModelLayerLocation(riftResource("boss_rift"), "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BOSS_RIFT_MODEL, RiftRenderer::createBodyLayer);
    }
}
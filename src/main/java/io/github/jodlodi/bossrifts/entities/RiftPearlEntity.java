package io.github.jodlodi.bossrifts.entities;

import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class RiftPearlEntity extends Entity  implements IRendersAsItem {
	private double tx;
	private double ty;
	private double tz;
	private int life;
	private boolean surviveAfterDeath;

	public RiftPearlEntity(EntityType<? extends RiftPearlEntity> type, World level) {
		super(type, level);
	}

	public RiftPearlEntity(World level, double x, double y, double z) {
		this(ModEntities.RIFT_PEARL.get(), level);
		this.life = 0;
		this.setPos(x, y, z);
	}

	public RiftPearlEntity(FMLPlayMessages.SpawnEntity ignoredSpawnEntity, World level) {
		this(ModEntities.RIFT_PEARL.get(), level);
	}

	@Override
	public ItemStack getItem() {
		return ModItems.RIFT_PEARL.get().getDefaultInstance();
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 4.0D;
		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	public void signalTo(Vector3d pos) {
		double d0 = pos.x();
		double i = pos.y();
		double d1 = pos.z();
		double d2 = d0 - this.getX();
		double d3 = d1 - this.getZ();
		float f = MathHelper.sqrt(d2 * d2 + d3 * d3);
		if (f > 12.0F) {
			this.tx = this.getX() + d2 / (double)f * 12.0D;
			this.tz = this.getZ() + d3 / (double)f * 12.0D;
			this.ty = this.getY() + 8.0D;
		} else {
			this.tx = d0;
			this.ty = i;
			this.tz = d1;
		}

		this.life = 0;
		this.surviveAfterDeath = this.random.nextInt(5) > 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
		this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
			this.yRot = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
			this.xRot = (float)(MathHelper.atan2(p_70016_3_, f) * (double)(180F / (float)Math.PI));
			this.yRotO = this.yRot;
			this.xRotO = this.xRot;
		}
	}

	@Override
	public void tick() {
		super.tick();
		Vector3d vector3d = this.getDeltaMovement();
		double d0 = this.getX() + vector3d.x;
		double d1 = this.getY() + vector3d.y;
		double d2 = this.getZ() + vector3d.z;
		float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
		this.xRot = ProjectileEntity.lerpRotation(this.xRotO, (float)(MathHelper.atan2(vector3d.y, f) * (double)(180F / (float)Math.PI)));
		this.yRot = ProjectileEntity.lerpRotation(this.yRotO, (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI)));
		if (!this.level.isClientSide) {
			double d3 = this.tx - d0;
			double d4 = this.tz - d2;
			float f1 = (float)Math.sqrt(d3 * d3 + d4 * d4);
			float f2 = (float)MathHelper.atan2(d4, d3);
			double d5 = MathHelper.lerp(0.0025D, f, f1);
			double d6 = vector3d.y;
			if (f1 < 1.0F) {
				d5 *= 0.8D;
				d6 *= 0.8D;
			}

			int j = this.getY() < this.ty ? 1 : -1;
			vector3d = new Vector3d(Math.cos(f2) * d5, d6 + ((double)j - d6) * (double)0.015F, Math.sin(f2) * d5);
			this.setDeltaMovement(vector3d);
		}

		if (this.isInWater()) {
			for(int i = 0; i < 4; ++i) {
				this.level.addParticle(ParticleTypes.BUBBLE, d0 - vector3d.x * 0.25D, d1 - vector3d.y * 0.25D, d2 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
			}
		} else {
			this.level.addParticle(ParticleTypes.PORTAL, d0 - vector3d.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vector3d.y * 0.25D - 0.5D, d2 - vector3d.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vector3d.x, vector3d.y, vector3d.z);
		}

		if (!this.level.isClientSide) {
			this.setPos(d0, d1, d2);
			++this.life;
			if (this.life > 80 && !this.level.isClientSide) {
				this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
				this.remove();
				if (this.surviveAfterDeath) {
					this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
				} else {
					this.level.levelEvent(2003, this.blockPosition(), 0);
				}
			}
		} else {
			this.setPosRaw(d0, d1, d2);
		}

	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {

	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {

	}

	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
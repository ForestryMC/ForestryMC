/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.tiles;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IHiveTile;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.genetics.IAllele;
import forestry.apiculture.BeekeepingLogic;
import forestry.apiculture.blocks.BlockBeeHives;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.config.Config;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileHive extends TileEntity implements ITickable, IHiveTile, IActivatable, IBeeHousing {
	private static final DamageSource damageSourceBeeHive = new DamageSourceForestry("bee.hive");

	private final InventoryAdapter contained = new InventoryAdapter(2, "Contained");
	private final HiveBeeHousingInventory inventory;
	private final BeekeepingLogic beeLogic;
	private final IErrorLogic errorLogic;
	private final Predicate<EntityLivingBase> beeTargetPredicate;

	@Nullable
	private IBee containedBee = null;
	private boolean active = false;
	private boolean angry = false;
	private int calmTime;

	/**
	 * Hack to make sure that hives glow.
	 * TODO: remove when Mojang fixes this bug: https://bugs.mojang.com/browse/MC-3329
	 */
	private boolean updatedLight;

	public TileHive() {
		inventory = new HiveBeeHousingInventory(this);
		beeLogic = new BeekeepingLogic(this);
		errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
		beeTargetPredicate = new BeeTargetPredicate(this);
	}

	@Override
	public void update() {
		if (Config.generateBeehivesDebug) {
			return;
		}

		if (world.isRemote) {
			if (!updatedLight && world.getWorldTime() % 100 == 0) {
				updatedLight = world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
			}
			if (active && world.rand.nextInt(4) == 0) {
				if (beeLogic.canDoBeeFX()) {
					beeLogic.doBeeFX();
				}
			}
		} else {
			boolean canWork = beeLogic.canWork(); // must be called every tick to stay updated

			if (world.rand.nextInt(angry ? 10 : 200) == 0) {
				if (calmTime == 0) {
					if (canWork) {
						AxisAlignedBB boundingBox = AlleleEffect.getBounding(getContainedBee().getGenome(), this);
						List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox, beeTargetPredicate);
						if (!entities.isEmpty()) {
							Collections.shuffle(entities);
							EntityLivingBase entity = entities.get(0);
							attack(entity, 2);
						}
						beeLogic.doWork();
					}
				} else {
					calmTime--;
				}
			}

			setActive(calmTime == 0);
		}
	}

	public IBee getContainedBee() {
		if (this.containedBee == null) {
			IBeeGenome beeGenome = null;
			ItemStack containedBee = contained.getStackInSlot(0);
			if (!containedBee.isEmpty()) {
				IBee bee = BeeManager.beeRoot.getMember(containedBee);
				if (bee != null) {
					beeGenome = bee.getGenome();
				}
			}
			if (beeGenome == null) {
				beeGenome = getGenomeFromBlock();
			}
			if (beeGenome == null) {
				beeGenome = BeeDefinition.FOREST.getGenome();
			}
			this.containedBee = BeeManager.beeRoot.getBee(beeGenome);
		}
		return this.containedBee;
	}

	@Nullable
	private IBeeGenome getGenomeFromBlock() {
		if (world.isBlockLoaded(pos)) {
			IBlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block instanceof BlockBeeHives) {
				IHiveRegistry.HiveType hiveType = BlockBeeHives.getHiveType(blockState);
				String speciesUid = hiveType.getSpeciesUid();
				IAllele[] template = BeeManager.beeRoot.getTemplate(speciesUid);
				if (template != null) {
					return BeeManager.beeRoot.templateAsGenome(template);
				}
			}
		}
		return null;
	}

	public void setContained(List<ItemStack> bees) {
		for (ItemStack itemstack : bees) {
			InventoryUtil.addStack(contained, itemstack, true);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		contained.readFromNBT(nbttagcompound);
		beeLogic.readFromNBT(nbttagcompound);
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		contained.writeToNBT(nbttagcompound);
		beeLogic.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void calmBees() {
		calmTime = 5;
		angry = false;
		setActive(false);
	}

	@Override
	public boolean isAngry() {
		return angry;
	}

	@Override
	public void onAttack(World world, BlockPos pos, EntityPlayer player) {
		if (calmTime == 0) {
			angry = true;
		}
	}

	@Override
	public void onBroken(World world, BlockPos pos, EntityPlayer player, boolean canHarvest) {
		if (calmTime == 0) {
			attack(player, 10);
		}

		if (canHarvest) {
			for (ItemStack beeStack : InventoryUtil.getStacks(contained)) {
				if (beeStack != null) {
					ItemStackUtil.dropItemStackAsEntity(beeStack, world, pos);
				}
			}
		}
	}

	private static void attack(EntityLivingBase entity, int maxDamage) {
		double attackAmount = entity.world.rand.nextDouble() / 2.0 + 0.5;
		int damage = (int) (attackAmount * maxDamage);
		if (damage > 0) {
			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, damageSourceBeeHive.damageType, true);
			if (entity.world.rand.nextInt(4) >= count) {
				entity.attackEntityFrom(damageSourceBeeHive, damage);
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		this.active = active;

		if (!world.isRemote) {
			NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), pos, world);
		}
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}


	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("active", calmTime == 0);
		beeLogic.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		setActive(tag.getBoolean("active"));
		beeLogic.readFromNBT(tag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.emptyList();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.emptyList();
	}


	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), getWorld(), getPos());
	}

	@Override
	public EnumHumidity getHumidity() {
		float humidity = ClimateUtil.getHumidity(getWorld(), getPos());
		return EnumHumidity.getFromValue(humidity);
	}

	@Override
	public int getBlockLightValue() {
		return getWorld().isDaytime() ? 15 : 0; // hives may have the sky obstructed but should still be active
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return true; // hives may have the sky obstructed but should still be active
	}

	@Override
	public boolean isRaining() {
		return world.isRainingAt(getPos().up());
	}

	@Override
	public World getWorldObj() {
		return world;
	}

	@Override
	public Biome getBiome() {
		return getWorld().getBiome(getPos());
	}

	@Override
	@Nullable
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		BlockPos pos = getPos();
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getPos();
	}

	private static class BeeTargetPredicate implements Predicate<EntityLivingBase> {

		private final IHiveTile hive;

		public BeeTargetPredicate(IHiveTile hive) {
			this.hive = hive;
		}

		@Override
		public boolean apply(@Nullable EntityLivingBase input) {
			if (input != null && input.isEntityAlive() && !input.isInvisible()) {
				if (input instanceof EntityPlayer) {
					return EntitySelectors.CAN_AI_TARGET.apply(input);
				} else if (hive.isAngry()) {
					return true;
				} else if (input instanceof IMob) {
					// don't attack semi-passive vanilla mobs
					return !(input instanceof EntityEnderman) && !(input instanceof EntityPigZombie);
				}
			}
			return false;
		}
	}

}

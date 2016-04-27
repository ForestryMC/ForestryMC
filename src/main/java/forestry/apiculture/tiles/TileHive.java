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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

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
import forestry.core.inventory.InventoryAdapter;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;

public class TileHive extends TileEntity implements ITickable, IHiveTile, IActivatable, IBeeHousing {
	private static final DamageSource damageSourceBeeHive = new DamageSourceForestry("bee.hive");

	@Nonnull
	private final InventoryAdapter contained = new InventoryAdapter(2, "Contained");
	@Nonnull
	private final HiveBeeHousingInventory inventory;
	@Nonnull
	private final BeekeepingLogic beeLogic;
	@Nonnull
	private final IErrorLogic errorLogic;
	private IBee containedBee = null;
	private boolean active = true;
	private boolean angry = false;
	private int calmTime;

	/**
	 * Hack to make sure that hives glow.
	 * TODO: remove when Mojang fixes this bug: https://bugs.mojang.com/browse/MC-3329
	 * Hives should not need to tick normally.
	 */
	private boolean updatedLight;

	public TileHive() {
		inventory = new HiveBeeHousingInventory(this);
		beeLogic = new BeekeepingLogic(this);
		errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	}

	@Override
	public void update() {
		if (worldObj.isRemote) {
			if (!updatedLight && worldObj.getWorldTime() % 20 == 0) {
				updatedLight = worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
			}
			if (active && worldObj.rand.nextInt(4) == 0) {
				if (beeLogic.canDoBeeFX()) {
					beeLogic.doBeeFX();
				}
			}
		} else {
			boolean canWork = beeLogic.canWork(); // must be called every tick to stay updated

			if (worldObj.rand.nextInt(angry ? 10 : 80) == 0) {
				if (calmTime == 0) {
					if (canWork) {
						AxisAlignedBB boundingBox = AlleleEffect.getBounding(getContainedBee().getGenome(), this);
						List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox);
						if (!entities.isEmpty()) {
							Collections.shuffle(entities);
							EntityLivingBase entity = entities.get(0);
							attack(entity, 2);
						}
						beeLogic.doWork();
						setActive(true);
					} else {
						setActive(false);
					}
				} else {
					calmTime--;
					if (calmTime == 0) {
						setActive(true);
					} else {
						setActive(false);
					}
				}
			}
		}
	}

	public IBee getContainedBee() {
		if (this.containedBee == null) {
			IBeeGenome beeGenome = null;
			ItemStack containedBee = contained.getStackInSlot(0);
			if (containedBee != null) {
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
			this.containedBee = BeeManager.beeRoot.getBee(worldObj, beeGenome);
		}
		return this.containedBee;
	}

	private IBeeGenome getGenomeFromBlock() {
		IBlockState blockState = worldObj.getBlockState(pos);
		if (blockState != null) {
			Block block = blockState.getBlock();
			if (block instanceof BlockBeeHives) {
				IHiveRegistry.HiveType hiveType = BlockBeeHives.getHiveType(blockState);
				if (hiveType != null) {
					String speciesUid = hiveType.getSpeciesUid();
					IAllele[] template = BeeManager.beeRoot.getTemplate(speciesUid);
					if (template != null) {
						return BeeManager.beeRoot.templateAsGenome(template);
					}
				}
			}
		}
		return null;
	}

	public void setContained(@Nonnull List<ItemStack> bees) {
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
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		contained.writeToNBT(nbttagcompound);
		beeLogic.writeToNBT(nbttagcompound);
	}

	@Override
	public void calmBees() {
		calmTime = 5;
		angry = false;
		setActive(false);
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
					ItemStackUtil.dropItemStackAsEntity(beeStack, worldObj, pos);
				}
			}
		}
	}

	private static void attack(EntityLivingBase entity, int maxDamage) {
		double attackAmount = entity.worldObj.rand.nextDouble() / 2.0 + 0.5;
		int damage = (int) (attackAmount * maxDamage);
		if (damage > 0) {
			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, damageSourceBeeHive.damageType, true);
			damage -= damage / 4.0f * count;
			if (damage > 0) {
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

		if (!worldObj.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		beeLogic.syncToClient();
		return super.getDescriptionPacket();
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
		float humidity = ForestryAPI.climateManager.getHumidity(getWorld(), getPos());
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
	public BiomeGenBase getBiome() {
		return getWorld().getBiomeGenForCoords(getPos());
	}

	@Override
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		BlockPos pos = getPos();
		return new Vec3(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
	}

	@Nonnull
	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getPos();
	}

}

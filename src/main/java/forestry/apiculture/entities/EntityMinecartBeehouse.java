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
package forestry.apiculture.entities;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.gui.IGuiBeeHousingInventory;
import forestry.apiculture.network.PacketBeeLogicEntityRequest;
import forestry.apiculture.tiles.TileAbstractBeeHousing;
import forestry.apiculture.tiles.TileBeehouse;
import forestry.core.access.AccessHandler;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gui.IHintSource;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.IStreamableGui;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.IFilterSlotDelegate;
import forestry.core.utils.InventoryUtil;

public class EntityMinecartBeehouse extends EntityMinecartContainer implements IBeeHousing, IFilterSlotDelegate, IGuiBeeHousingInventory, IStreamableGui, IRestrictedAccess, IClimatised, IHintSource {
	private static final Random random = new Random();
	private static final int beeFXInterval = 4;
	private static final int pollenFXInterval = 50;

	private final int beeFXTime = random.nextInt(beeFXInterval);
	private final int pollenFXTime = random.nextInt(pollenFXInterval);

	private static final Iterable<IBeeModifier> beeModifiers = ImmutableList.<IBeeModifier>of(new TileBeehouse.BeehouseBeeModifier());
	private static final Iterable<IBeeListener> beeListeners = ImmutableList.<IBeeListener>of(new DefaultBeeListener());

	private final IBeekeepingLogic beeLogic;
	private final IErrorLogic errorLogic;
	private final BeeCartInventory beeInventory;
	private final AccessHandler accessHandler;

	// CLIENT
	private int breedingProgressPercent = 0;
	private boolean needsActiveUpdate = true;

	public EntityMinecartBeehouse(World world) {
		super(world);
		setHasDisplayTile(true);
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
		this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
		this.beeInventory = new BeeCartInventory(this);
		this.accessHandler = new AccessHandler(this);
	}

	public EntityMinecartBeehouse(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		setHasDisplayTile(true);
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
		this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
		this.beeInventory = new BeeCartInventory(this);
		this.accessHandler = new AccessHandler(this);
	}

	@Override
	public final boolean interactFirst(EntityPlayer player) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player))) {
			return true;
		}

		if (!worldObj.isRemote) {
			player.openGui(ForestryAPI.instance, GuiId.MinecartBeehouseGUI.ordinal(), worldObj, getEntityId(), -1, 0);
		}
		return true;
	}

	public void setOwner(GameProfile owner) {
		accessHandler.setOwner(owner);
	}

	@Override
	public int getMinecartType() {
		return -1;
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}

	/* IBeeHousing */
	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return beeModifiers;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return beeListeners;
	}

	@Override
	public BeeCartInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), (int) posX, (int) posY, (int) posZ);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().rainfall);
	}

	@Override
	public float getExactTemperature() {
		return getBiome().temperature;
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall;
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getBlockLightValue((int) posX, (int) posY, (int) posZ);
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeTheSky((int) posX, (int) posY, (int) posZ);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public BiomeGenBase getBiome() {
		return worldObj.getBiomeGenForCoords((int) posX, (int) posZ);
	}

	@Override
	public GameProfile getOwner() {
		return accessHandler.getOwner();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates((int) posX, (int) posY, (int) posZ);
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return Vec3.createVectorHelper(posX, posY + 0.25, posZ);
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		breedingProgressPercent = data.readVarInt();
	}

	/**
	 * Returns scaled queen health or breeding progress
	 */
	public int getHealthScaled(int i) {
		return (breedingProgressPercent * i) / 100;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (beeLogic.canWork()) {
				beeLogic.doWork();
			}
		} else {
			if (needsActiveUpdate) {
				IForestryPacketServer packet = new PacketBeeLogicEntityRequest(this);
				Proxies.net.sendToServer(packet);
				needsActiveUpdate = false;
			}

			if (beeLogic.canDoBeeFX()) {
				if (worldObj.getTotalWorldTime() % beeFXInterval == beeFXTime) {
					beeLogic.doBeeFX();
				}

				if (worldObj.getTotalWorldTime() % pollenFXInterval == pollenFXTime) {
					TileAbstractBeeHousing.doPollenFX(worldObj, posX - 0.5, posY - 0.1, posZ - 0.5);
				}
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		beeLogic.readFromNBT(nbtTagCompound);
		accessHandler.readFromNBT(nbtTagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		beeLogic.writeToNBT(nbtTagCompound);
		accessHandler.writeToNBT(nbtTagCompound);
	}

	/* IFilterSlotDelegate */
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		EnumBeeType beeType = BeeManager.beeRoot.getType(itemStack);

		if (slotIndex == BeeCartInventory.SLOT_QUEEN) {
			return beeType == EnumBeeType.QUEEN || beeType == EnumBeeType.PRINCESS;
		} else if (slotIndex == BeeCartInventory.SLOT_DRONE) {
			return beeType == EnumBeeType.DRONE;
		}
		return false;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		return "entity." + getEntityString() + ".name";
	}

	@Override
	public boolean canBeRidden() {
		return false;
	}

	@Override
	public Block func_145820_n() {
		return ForestryBlock.apiculture.block();
	}

	@Override
	public int getDisplayTileData() {
		return Constants.DEFINITION_BEEHOUSE_META;
	}

	@Override
	public void killMinecart(DamageSource damageSource) {
		super.killMinecart(damageSource);

		if (!damageSource.isExplosion()) {
			entityDropItem(new ItemStack(func_145820_n(), 1, getDisplayTileData()), 0.0F);
		}
	}

	@Override
	public void setDead() {
		// stop ghost items from dropping client side
		if (worldObj.isRemote) {
			for (int slot = 0; slot < getSizeInventory(); slot++) {
				setInventorySlotContents(slot, null);
			}
		}
		super.setDead();
	}

	@Override
	public ItemStack getCartItem() {
		return ForestryItem.minecartBeehouse.getItemStack();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return canSlotAccept(slot, itemStack);
	}

	@Override
	public IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {

	}

	@Override
	public String[] getHints() {
		return Config.hints.get("apiary");
	}

	private static class BeeCartInventory implements IBeeHousingInventory {
		public static final int SLOT_QUEEN = 0;
		public static final int SLOT_DRONE = 1;
		public static final int SLOT_PRODUCT_1 = 2;
		public static final int SLOT_PRODUCT_COUNT = 7;

		private final EntityMinecartBeehouse cart;

		public BeeCartInventory(EntityMinecartBeehouse cart) {
			this.cart = cart;
		}

		@Override
		public final ItemStack getQueen() {
			return cart.getStackInSlot(SLOT_QUEEN);
		}

		@Override
		public final ItemStack getDrone() {
			return cart.getStackInSlot(SLOT_DRONE);
		}

		@Override
		public final void setQueen(ItemStack itemstack) {
			cart.setInventorySlotContents(SLOT_QUEEN, itemstack);
		}

		@Override
		public final void setDrone(ItemStack itemstack) {
			cart.setInventorySlotContents(SLOT_DRONE, itemstack);
		}

		@Override
		public final boolean addProduct(ItemStack product, boolean all) {
			return InventoryUtil.tryAddStack(cart, product, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, all, true);
		}
	}

}

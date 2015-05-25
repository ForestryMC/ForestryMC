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
package forestry.apiculture.gadgets;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TileForestry;
import forestry.core.interfaces.IClimatised;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.Utils;

public class TileBeehouse extends TileBase implements IBeeHousing, IClimatised {
	// CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_PRODUCT_1 = 2;
	public static final int SLOT_PRODUCT_COUNT = 7;
	public static final int SLOT_FRAMES_1 = 9;
	public static final int SLOT_FRAMES_COUNT = 3;
	private final IBeekeepingLogic logic;
	private BiomeGenBase biome;
	private int displayHealthMax = 0;
	private int displayHealth = 0;

	public TileBeehouse() {
		setHints(Config.hints.get("apiary"));
		logic = BeeManager.beeRoot.createBeekeepingLogic(this);
		setInternalInventory(new BeehouseInventoryAdapter(this, 12, "Items").disableAutomation());
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.BeehouseGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("BiomeId", biome.biomeID);
		if (logic != null) {
			logic.writeToNBT(nbttagcompound);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		int biomeId = nbttagcompound.getInteger("BiomeId");
		biome = BiomeGenBase.getBiome(biomeId);
		logic.readFromNBT(nbttagcompound);

	}

	@Override
	public void initialize() {
		super.initialize();
		updateBiome();
	}

	@Override
	public void validate() {
		updateBiome();
	}

	/* ICLIMATISED */
	@Override
	public boolean isClimatized() {
		return true;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(biome, xCoord, yCoord, zCoord);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Deprecated
	@Override
	public void setErrorState(IErrorState state) {
		removeErrorStates();
		if (state != EnumErrorCode.OK) {
			addErrorState(state);
		}
	}

	@Deprecated
	@Override
	public IErrorState getErrorState() {
		Set<IErrorState> errorStates = getErrorStates();
		if (errorStates.size() == 0) {
			return EnumErrorCode.OK;
		} else {
			return getErrorStates().iterator().next();
		}
	}

	@Override
	public float getExactTemperature() {
		return biome.getFloatTemperature(xCoord, yCoord, zCoord);
	}

	@Override
	public float getExactHumidity() {
		return biome.rainfall;
	}

	/* UPDATING */
	@Override
	public void updateClientSide() {

		// / Multiplayer FX
		if (BeeManager.beeRoot.isMated(getInternalInventory().getStackInSlot(SLOT_QUEEN))) {
			if (!hasErrorState() && updateOnInterval(2)) {
				IBee displayQueen = BeeManager.beeRoot.getMember(getInternalInventory().getStackInSlot(SLOT_QUEEN));
				displayQueen.doFX(logic.getEffectData(), this);
			}
		}

	}

	@Override
	public void updateServerSide() {

		if (logic.canWork())
			logic.doWork();

		IBee queen = logic.getQueen();
		if (queen == null) {
			return;
		}

		// Add swarm effects
		if (updateOnInterval(200)) {
			onQueenChange(getInternalInventory().getStackInSlot(SLOT_QUEEN));
		}
	}

	// @Override
	public boolean isWorking() {
		return !hasErrorState();
	}

	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		return InvTools.tryAddStack(getInternalInventory(), product, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, all, true);
	}

	/* NETWORK SYNCH */
	@Override
	public void onQueenChange(ItemStack queenStack) {
		if (!Proxies.common.isSimulating(worldObj)) {
			return;
		}

		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack), xCoord, yCoord,
				zCoord);
		Proxies.net.sendNetworkPacket(new PacketTileUpdate(this), xCoord, yCoord, zCoord);
	}

	/* STATE INFORMATION */
	private int getHealthDisplay() {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_QUEEN) == null) {
			return 0;
		}

		if (ForestryItem.beeQueenGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN))) {
			return BeeManager.beeRoot.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getHealth();
		} else if (ForestryItem.beePrincessGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN))) {
			return displayHealth;
		} else {
			return 0;
		}
	}

	private int getMaxHealthDisplay() {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_QUEEN) == null) {
			return 0;
		}

		if (ForestryItem.beeQueenGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN))) {
			return BeeManager.beeRoot.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getMaxHealth();
		} else if (ForestryItem.beePrincessGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN))) {
			return displayHealthMax;
		} else {
			return 0;
		}
	}

	/**
	 * Returns scaled queen health or breeding progress
	 */
	public int getHealthScaled(int i) {
		if (getMaxHealthDisplay() == 0) {
			return 0;
		}

		return (getHealthDisplay() * i) / getMaxHealthDisplay();
	}

	public void updateBiome() {
		if (worldObj != null) {
			BiomeGenBase biome = worldObj.getBiomeGenForCoordsBody(xCoord, zCoord);
			if (biome != null) {
				this.biome = biome;
			}
		}
	}

	/* SMP */
	// @Override
	public void getGUINetworkData(int i, int j) {
		if (logic == null) {
			return;
		}

		switch (i) {
			case 0:
				displayHealth = j;
				break;
			case 1:
				displayHealthMax = j;
				break;
		}
	}

	// @Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		if (logic == null) {
			return;
		}

		iCrafting.sendProgressBarUpdate(container, 0, logic.getBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 1, logic.getTotalBreedingTime());
	}

	// / IBEEHOUSING
	@Override
	public int getXCoord() {
		return xCoord;
	}

	@Override
	public int getYCoord() {
		return yCoord;
	}

	@Override
	public int getZCoord() {
		return zCoord;
	}

	@Override
	public BiomeGenBase getBiome() {
		return biome;
	}

	@Override
	public ItemStack getQueen() {
		return getStackInSlot(SLOT_QUEEN);
	}

	@Override
	public ItemStack getDrone() {
		return getStackInSlot(SLOT_DRONE);
	}

	@Override
	public void setQueen(ItemStack itemstack) {
		setInventorySlotContents(SLOT_QUEEN, itemstack);
	}

	@Override
	public void setDrone(ItemStack itemstack) {
		setInventorySlotContents(SLOT_DRONE, itemstack);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public boolean canBreed() {
		return true;
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome, float currentModifier) {
		return 0.25f;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		return 0.0f;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		return 3.0f;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
		return 3.0f;
	}

	@Override
	public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
		return 0.0f;
	}

	@Override
	public void wearOutEquipment(int amount) {
	}

	@Override
	public boolean isSealed() {
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		return false;
	}

	@Override
	public boolean isHellish() {
		return false;
	}

	@Override
	public void onQueenDeath(IBee queen) {
	}

	@Override
	public void onPostQueenDeath(IBee queen) {
	}

	@Override
	public boolean onPollenRetrieved(IBee queen, IIndividual pollen, boolean isHandled) {
		return false;
	}

	@Override
	public boolean onEggLaid(IBee queen) {
		return false;
	}

	/* IHousing */
	@Override
	public GameProfile getOwnerName() {
		return this.getOwnerProfile();
	}

	protected static class BeehouseInventoryAdapter extends TileInventoryAdapter<TileBeehouse> {
		public BeehouseInventoryAdapter(TileBeehouse tile, int size, String name) {
			super(tile, size, name);
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_QUEEN) {
				return BeeManager.beeRoot.isMember(itemStack) && !BeeManager.beeRoot.isDrone(itemStack);
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_FRAMES_1, SLOT_FRAMES_COUNT)) {
				return itemStack.getItem() instanceof IHiveFrame;
			} else if (slotIndex == SLOT_DRONE) {
				return BeeManager.beeRoot.isDrone(itemStack);
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return GuiUtil.isIndexInRange(slotIndex, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT);
		}
	}
}

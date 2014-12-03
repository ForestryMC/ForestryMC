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

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumErrorCode;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.IClimatised;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.Utils;
import forestry.plugins.PluginApiculture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class TileBeehouse extends TileBase implements IBeeHousing, IClimatised {

	// CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_INVENTORY_1 = 2;
	public static final int SLOT_PRODUCT_1 = 2;
	public static final int SLOT_PRODUCT_COUNT = 7;
	public static final int SLOT_FRAMES_1 = 9;
	public static final int SLOT_FRAMES_2 = 10;
	public static final int SLOT_FRAMES_3 = 11;
	public static final int SLOT_INVENTORY_COUNT = 7;
	public static final int SLOT_FRAMES_COUNT = 3;

	// Inventory
	protected final InventoryAdapter inventory = new InventoryAdapter(12, "Items");
	private final IBeekeepingLogic logic;
	private BiomeGenBase biome;
	private int displayHealthMax = 0;
	private int displayHealth = 0;

	public TileBeehouse() {
		setHints(Config.hints.get("apiary"));
		logic = PluginApiculture.beeInterface.createBeekeepingLogic(this);
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

		inventory.writeToNBT(nbttagcompound);
		if (logic != null)
			logic.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		int biomeId = nbttagcompound.getInteger("BiomeId");
		biome = BiomeGenBase.getBiome(biomeId);

		inventory.readFromNBT(nbttagcompound);
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
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return biome.temperature;
	}

	@Override
	public float getExactHumidity() {
		return biome.rainfall;
	}

	/* UPDATING */
	@Override
	public void updateClientSide() {

		// / Multiplayer FX
		if (PluginApiculture.beeInterface.isMated(inventory.getStackInSlot(SLOT_QUEEN))) {
			if (getErrorState() == EnumErrorCode.OK && worldObj.getTotalWorldTime() % 2 % 2 == 0) {
				IBee displayQueen = PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(SLOT_QUEEN));
				displayQueen.doFX(logic.getEffectData(), this);
			}
		}

	}

	@Override
	public void updateServerSide() {

		logic.update();

		IBee queen = logic.getQueen();
		if (queen == null)
			return;

		// Add swarm effects
		if (worldObj.getTotalWorldTime() % 200 * 10 == 0)
			onQueenChange(inventory.getStackInSlot(SLOT_QUEEN));
		/* These should get already done on the client / doesn't work server-side anyway
		 if (getErrorState() == EnumErrorCode.OK && worldObj.getTotalWorldTime() % 2 % 2 == 0)
		 queen.doFX(logic.getEffectData(), this);

		 if (getErrorState() == EnumErrorCode.OK && worldObj.getTotalWorldTime() % 50 == 0) {
		 float f = xCoord + 0.5F;
		 float f1 = yCoord + 0.0F + (worldObj.rand.nextFloat() * 6F) / 16F;
		 float f2 = zCoord + 0.5F;
		 float f3 = 0.52F;
		 float f4 = worldObj.rand.nextFloat() * 0.6F - 0.3F;

		 Proxies.common.addEntitySwarmFX(worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
		 Proxies.common.addEntitySwarmFX(worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
		 Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
		 Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
		 }*/

	}

	// @Override
	public boolean isWorking() {
		return getErrorState() == EnumErrorCode.OK;
	}

	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		return inventory.tryAddStack(product, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, all, true);
	}

	/* NETWORK SYNCH */
	@Override
	public void onQueenChange(ItemStack queenStack) {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack), xCoord, yCoord,
				zCoord);
		Proxies.net.sendNetworkPacket(new PacketTileUpdate(this), xCoord, yCoord, zCoord);
	}

	/* STATE INFORMATION */
	private int getHealthDisplay() {
		if (inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (ForestryItem.beeQueenGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN)))
			return PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getHealth();
		else if (ForestryItem.beePrincessGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN)))
			return displayHealth;
		else
			return 0;
	}

	private int getMaxHealthDisplay() {
		if (inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (ForestryItem.beeQueenGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN)))
			return PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getMaxHealth();
		else if (ForestryItem.beePrincessGE.isItemEqual(inventory.getStackInSlot(SLOT_QUEEN)))
			return displayHealthMax;
		else
			return 0;
	}

	/**
	 * Returns scaled queen health or breeding progress
	 *
	 * @param i
	 * @return
	 */
	public int getHealthScaled(int i) {
		if (getMaxHealthDisplay() == 0)
			return 0;

		return (getHealthDisplay() * i) / getMaxHealthDisplay();
	}

	public int getTemperatureScaled(int i) {
		return Math.round(i * (getExactTemperature() / 2));
	}

	public int getHumidityScaled(int i) {
		return Math.round(i * getExactHumidity());
	}

	public void updateBiome() {
		if (worldObj != null) {
			BiomeGenBase biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
			if (biome != null) {
				this.biome = biome;
				setErrorState(EnumErrorCode.OK);
			}
		}
	}

	/* SMP */
	// @Override
	public void getGUINetworkData(int i, int j) {
		if (logic == null)
			return;

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
		if (logic == null)
			return;

		iCrafting.sendProgressBarUpdate(container, 0, logic.getBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 1, logic.getTotalBreedingTime());
	}

	/* INVENTORY MANAGMENT */
	@Override
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	public void setSlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
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
	public int getBiomeId() {
		return biome.biomeID;
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
		setSlotContents(SLOT_QUEEN, itemstack);
	}

	@Override
	public void setDrone(ItemStack itemstack) {
		setSlotContents(SLOT_DRONE, itemstack);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public void setErrorState(int state) {
		setErrorState(EnumErrorCode.values()[state]);
	}

	@Override
	public int getErrorOrdinal() {
		return getErrorState().ordinal();
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
}

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

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.core.ITileStructure;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public class TileAlvearyPlain extends TileAlveary implements ISidedInventory, IBeeHousing, IClimatised, IHintSource {

	// / CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_PRODUCT_1 = 2;
	public static final int SLOT_PRODUCTION_COUNT = 7;
	public static final int BLOCK_META = 0;

	// / MEMBERS
	protected IBeekeepingLogic beekeepingLogic;
	protected BiomeGenBase biome;
	protected float tempChange = 0.0f;
	protected float humidChange = 0.0f;
	private int displayHealthMax = 0;
	private int displayHealth = 0;

	public TileAlvearyPlain() {
		super(0);
	}

	@Override
	public void openGui(EntityPlayer player) {
		if (isMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		} else if (this.hasMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, masterX, masterY, masterZ);
		}
	}

	@Override
	public void validate() {
		super.validate();
		setBiomeInformation();
	}

	private void setBiomeInformation() {
		this.biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		this.tempChange = nbttagcompound.getFloat("TempChange");
		this.humidChange = nbttagcompound.getFloat("HumidChange");

		if (beekeepingLogic != null) {
			beekeepingLogic.readFromNBT(nbttagcompound);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setFloat("TempChange", tempChange);
		nbttagcompound.setFloat("HumidChange", humidChange);

		if (beekeepingLogic != null) {
			beekeepingLogic.writeToNBT(nbttagcompound);
		}
	}

	/* UPDATING */
	@Override
	public void initialize() {
		super.initialize();
		setBiomeInformation();
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (beekeepingLogic == null) {
			return;
		}
		if (!isMaster()) {
			return;
		}

		if (beekeepingLogic.canWork())
			beekeepingLogic.doWork();

		// Equalize humidity and temperature
		equalizeTemperature();
		equalizeHumidity();

		IBee queen = beekeepingLogic.getQueen();
		if (queen == null) {
			return;
		}

		// Add swarm effects
		if (updateOnInterval(200)) {
			ISidedInventory inventory = getStructureInventory();
			if (inventory != null) {
				onQueenChange(inventory.getStackInSlot(SLOT_QUEEN));
			}
		}

		if (!hasErrorState()) {
			queen.doFX(beekeepingLogic.getEffectData(), this);

			if (updateOnInterval(50)) {
				float f = xCoord + 0.5F;
				float f1 = yCoord + 0.0F + (worldObj.rand.nextFloat() * 6F) / 16F;
				float f2 = zCoord + 0.5F;
				float f3 = 0.52F;
				float f4 = worldObj.rand.nextFloat() * 0.6F - 0.3F;

				Proxies.common.addEntitySwarmFX(worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
			}
		}
	}

	@Override
	protected void updateClientSide() {

		if (!isMaster()) {
			return;
		}

		if (hasErrorState()) {
			return;
		}

		if (updateOnInterval(2)) {
			ISidedInventory inventory = getStructureInventory();
			if (inventory == null) {
				return;
			}

			// / Multiplayer FX
			ItemStack queenStack = inventory.getStackInSlot(SLOT_QUEEN);
			if (BeeManager.beeRoot.isMated(queenStack)) {
				IBee displayQueen = BeeManager.beeRoot.getMember(queenStack);
				displayQueen.doFX(beekeepingLogic.getEffectData(), this);
			}
		}
	}

	private void equalizeTemperature() {
		if (tempChange == 0) {
			return;
		}

		tempChange -= 0.05f * tempChange;
		if (tempChange <= 0.001f && tempChange >= -0.001f) {
			tempChange = 0;
		}
	}

	private void equalizeHumidity() {
		if (humidChange == 0) {
			return;
		}

		humidChange -= 0.05f * humidChange;
		if (humidChange <= 0.001f && humidChange >= 0.001f) {
			humidChange = 0;
		}
	}

	/* STATE INFORMATION */
	private int getHealthDisplay() {
		IInventory inventory = getStructureInventory();
		if (inventory == null || inventory.getStackInSlot(SLOT_QUEEN) == null) {
			return 0;
		}

		if (BeeManager.beeRoot.isMated(inventory.getStackInSlot(SLOT_QUEEN))) {
			return BeeManager.beeRoot.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getHealth();
		} else if (!BeeManager.beeRoot.isDrone(inventory.getStackInSlot(SLOT_QUEEN))) {
			return displayHealth;
		} else {
			return 0;
		}
	}

	private int getMaxHealthDisplay() {
		IInventory inventory = getStructureInventory();
		if (inventory == null || inventory.getStackInSlot(SLOT_QUEEN) == null) {
			return 0;
		}

		if (BeeManager.beeRoot.isMated(inventory.getStackInSlot(SLOT_QUEEN))) {
			return BeeManager.beeRoot.getMember(inventory.getStackInSlot(SLOT_QUEEN)).getMaxHealth();
		} else if (!BeeManager.beeRoot.isDrone(inventory.getStackInSlot(SLOT_QUEEN))) {
			return displayHealthMax;
		} else {
			return 0;
		}
	}

	public int getHealthScaled(int i) {
		if (getMaxHealthDisplay() == 0) {
			return 0;
		}

		return (getHealthDisplay() * i) / getMaxHealthDisplay();
	}

	/* STRUCTURE MANAGMENT */
	@Override
	protected void createInventory() {
		setInternalInventory(new AlvearyInventoryAdapter(this));
	}

	@Override
	public void makeMaster() {
		super.makeMaster();
		if (beekeepingLogic == null) {
			this.beekeepingLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
		}
	}

	@Override
	public void onStructureReset() {
		super.onStructureReset();
		modifiers.clear();
		eventHandlers.clear();
	}

	/* IALVEARYCOMPONENT */
	private final Set<IBeeModifier> modifiers = new LinkedHashSet<IBeeModifier>();
	private final Set<IBeeListener> eventHandlers = new LinkedHashSet<IBeeListener>();

	@Override
	public void registerBeeModifier(IBeeModifier modifier) {
		modifiers.add(modifier);
	}

	@Override
	public void removeBeeModifier(IBeeModifier modifier) {
		modifiers.remove(modifier);
	}

	@Override
	public void registerBeeListener(IBeeListener modifier) {
		eventHandlers.add(modifier);
	}

	@Override
	public void removeBeeListener(IBeeListener modifier) {
		eventHandlers.remove(modifier);
	}

	@Override
	public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {
		float temperature = biome.temperature;
		tempChange = Math.min(boundaryUp - temperature, Math.max(boundaryDown - temperature, tempChange + change));
	}

	@Override
	public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {
		float humidity = biome.rainfall;
		humidChange = Math.min(boundaryUp - humidity, Math.max(boundaryDown - humidity, humidChange + change));
	}

	/* IBEEHOUSING */
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
	public BiomeGenBase getBiome() {
		return biome;
	}

	@Override
	public EnumTemperature getTemperature() {
		if (BiomeHelper.isBiomeHellish(biome) && tempChange >= 0) {
			return EnumTemperature.HELLISH;
		}

		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
		float mod = 2.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getTerritoryModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome, float currentModifier) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getProductionModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getMutationModifier(genome, mate, mod);
		}
		return mod;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getLifespanModifier(genome, mate, mod);
		}
		return mod;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getFloweringModifier(genome, mod);
		}
		return mod;
	}

	@Override
	public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getGeneticDecay(genome, mod);
		}
		return mod;
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
	public boolean addProduct(ItemStack product, boolean all) {
		IInventoryAdapter inventory = getStructureInventory();
		if (inventory == null) {
			return false;
		}

		return InvTools.tryAddStack(inventory, product, SLOT_PRODUCT_1, inventory.getSizeInventory() - SLOT_PRODUCT_1, all);
	}

	@Deprecated
	@Override
	public void setErrorState(IErrorState state) {
		removeErrorStates();
		if (state != EnumErrorCode.OK) {
			addErrorState(state);
		}
	}

	@Override
	public void wearOutEquipment(int amount) {
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.wearOutEquipment(amount);
		}
	}

	@Override
	public void onQueenChange(ItemStack queenStack) {
		if (!Proxies.common.isSimulating(worldObj)) {
			return;
		}

		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack), xCoord, yCoord,
				zCoord);

		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.onQueenChange(queenStack);
		}
	}

	@Override
	public void onQueenDeath(IBee queen) {
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.onQueenDeath(queen);
		}
	}

	@Override
	public void onPostQueenDeath(IBee queen) {
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.onPostQueenDeath(queen);
		}
	}

	@Override
	public boolean onPollenRetrieved(IBee queen, IIndividual pollen, boolean isHandled) {

		for (IBeeListener eventHandler : eventHandlers) {
			if (eventHandler.onPollenRetrieved(queen, pollen, isHandled)) {
				isHandled = true;
			}
		}

		return isHandled;
	}

	@Override
	public boolean onEggLaid(IBee queen) {
		for (IBeeListener eventHandler : eventHandlers) {
			if (eventHandler.onEggLaid(queen)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isSealed() {
		for (IBeeModifier modifier : modifiers) {
			if (modifier.isSealed()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		for (IBeeModifier modifier : modifiers) {
			if (modifier.isSelfLighted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		for (IBeeModifier modifier : modifiers) {
			if (modifier.isSunlightSimulated()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (IBeeModifier modifier : modifiers) {
			if (modifier.isHellish()) {
				return true;
			}
		}
		return false;
	}

	/* IINVENTORY */
	@Override
	public IInventoryAdapter getInternalInventory() {
		IInventoryAdapter inventory = super.getInternalInventory();
		if (isMaster()) {
			return inventory;
		} else if (hasMaster()) {
			ITileStructure central = getCentralTE();
			if (central instanceof TileAlveary) {
				return ((TileAlveary) central).getInternalInventory();
			}
		}
		return inventory;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				displayHealth = j;
				break;
			case 1:
				displayHealthMax = j;
				break;
			case 4:
				this.tempChange = (float) j / 100;
				break;
			case 5:
				this.humidChange = (float) j / 100;
				break;
			case 6: {
				this.biome = BiomeGenBase.getBiome(j);
				break;
			}
		}

	}

	public void sendGUINetworkData(ContainerAlveary container, ICrafting iCrafting) {
		if (beekeepingLogic == null) {
			return;
		}

		iCrafting.sendProgressBarUpdate(container, 0, beekeepingLogic.getBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 1, beekeepingLogic.getTotalBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 4, Math.round(tempChange * 100));
		iCrafting.sendProgressBarUpdate(container, 5, Math.round(humidChange * 100));
		iCrafting.sendProgressBarUpdate(container, 6, biome.biomeID);
	}

	/* IERRORSOURCE */
	@Deprecated
	@Override
	public IErrorState getErrorState() {
		Set<IErrorState> errorStates = null;
		if (hasMaster()) {
			ITileStructure tile = getCentralTE();
			if (tile != null) {
				errorStates = ((IErrorSource) tile).getErrorStates();
			}
		}

		if (errorStates == null) {
			errorStates = this.getErrorStates();
		}

		if (errorStates.size() == 0) {
			return EnumErrorCode.OK;
		} else {
			return getErrorStates().iterator().next();
		}
	}

	/* ICLIMATISED */
	@Override
	public boolean isClimatized() {
		return true;
	}

	@Override
	public float getExactTemperature() {
		return biome.temperature + this.tempChange;
	}

	@Override
	public float getExactHumidity() {
		return biome.rainfall + this.humidChange;
	}

	/* IHINTSOURCE */
	@Override
	public boolean hasHints() {
		return Config.hints.get("alveary").length > 0;
	}

	@Override
	public String[] getHints() {
		return Config.hints.get("alveary");
	}

	/* IOWNABLE */
	@Override
	public boolean isOwnable() {
		return true;
	}

	/* IHousing */
	@Override
	public GameProfile getOwnerName() {
		return this.getOwnerProfile();
	}

	private static class AlvearyInventoryAdapter extends TileInventoryAdapter<TileAlvearyPlain> {
		public AlvearyInventoryAdapter(TileAlvearyPlain tileAlvearyPlain) {
			super(tileAlvearyPlain, 9, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_QUEEN) {
				return BeeManager.beeRoot.isMember(itemStack) && !BeeManager.beeRoot.isDrone(itemStack);
			} else if (slotIndex == SLOT_DRONE) {
				return BeeManager.beeRoot.isDrone(itemStack);
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex != SLOT_QUEEN && slotIndex != SLOT_DRONE;
		}
	}
}

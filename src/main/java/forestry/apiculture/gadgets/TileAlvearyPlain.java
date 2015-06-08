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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
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
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.interfaces.IActivatable;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketHelper;
import forestry.core.network.PacketId;
import forestry.core.network.PacketInventoryStack;
import forestry.core.proxy.Proxies;

public class TileAlvearyPlain extends TileAlveary implements ISidedInventory, IBeeHousing, IClimatised, IHintSource, IActivatable {

	// / CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_PRODUCT_1 = 2;
	public static final int SLOT_PRODUCT_COUNT = 7;
	public static final int BLOCK_META = 0;

	// / MEMBERS
	protected IBeekeepingLogic beekeepingLogic;
	protected BiomeGenBase biome;
	protected float tempChange = 0.0f;
	protected float humidChange = 0.0f;

	// CLIENT
	private boolean active;
	private int displayHealthMax = 0;
	private int displayHealth = 0;
	private IBee displayQueen;

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
		this.biome = worldObj.getBiomeGenForCoordsBody(xCoord, zCoord);
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

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
		if (active) {
			ItemStack queen = getStackInSlot(SLOT_QUEEN);
			PacketHelper.writeItemStack(queen, data);
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		active = data.readBoolean();
		if (active) {
			ItemStack queen = PacketHelper.readItemStack(data);
			setInventorySlotContents(SLOT_QUEEN, queen);
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
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this));
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

		boolean canWork = beekeepingLogic.canWork();
		setActive(canWork);
		if (canWork) {
			beekeepingLogic.doWork();
		}

		// Equalize humidity and temperature
		equalizeTemperature();
		equalizeHumidity();
	}

	@Override
	protected void updateClientSide() {
		if (isMaster() && active && (displayQueen != null) && updateOnInterval(2)) {
			displayQueen.doFX(beekeepingLogic.getEffectData(), this);

			if (updateOnInterval(50)) {
				float fxX = xCoord + 0.5F;
				float fxY = yCoord + 0.25F + (worldObj.rand.nextFloat() * 6F) / 16F;
				float fxZ = zCoord + 0.5F;
				float f3 = 1.6F;
				float f4 = worldObj.rand.nextFloat() * f3 - 0.5F;

				Proxies.common.addEntitySwarmFX(worldObj, (fxX - f3), fxY, (fxZ + f4), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + f3), fxY, (fxZ + f4), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + f4), fxY, (fxZ - f3), 0F, 0F, 0F);
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + f4), fxY, (fxZ + f3), 0F, 0F, 0F);
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
		if (displayQueen == null) {
			return 0;
		}

		if (displayQueen.getMate() != null) {
			return displayQueen.getHealth();
		} else {
			return displayHealth;
		}
	}

	private int getMaxHealthDisplay() {
		if (displayQueen == null) {
			return 0;
		}

		if (displayQueen.getMate() != null) {
			return displayQueen.getMaxHealth();
		} else {
			return displayHealthMax;
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
		float temperature = biome.getFloatTemperature(xCoord, yCoord, zCoord);
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
	public ItemStack decrStackSize(int slotIndex, int amount) {
		ItemStack itemStack = super.decrStackSize(slotIndex, amount);
		if (slotIndex == SLOT_QUEEN) {
			handleQueenChange();
		}
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		super.setInventorySlotContents(slotIndex, itemStack);
		if (slotIndex == SLOT_QUEEN) {
			handleQueenChange();
		}
	}

	private void handleQueenChange() {
		if (!Proxies.common.isSimulating(worldObj)) {
			TileAlvearyPlain master = (TileAlvearyPlain) getCentralTE();
			if (master != null) {
				ItemStack itemStack = getStackInSlot(SLOT_QUEEN);
				master.displayQueen = BeeManager.beeRoot.getMember(itemStack);
			}
		}
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

		return InvTools.tryAddStack(inventory, product, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT, all);
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

		PacketInventoryStack packet = new PacketInventoryStack(PacketId.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack);
		Proxies.net.sendNetworkPacket(packet);

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
		return biome.getFloatTemperature(xCoord, yCoord, zCoord) + this.tempChange;
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

	/* IHousing */
	@Override
	public GameProfile getOwnerName() {
		return this.getOwner();
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

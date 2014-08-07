/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gadgets;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITrigger;
import buildcraft.api.inventory.ISpecialInventory;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IHintSource;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.Utils;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginEnergy;
import forestry.plugins.PluginFactory;
import forestry.plugins.PluginMail;

public class TileMachine extends TileForestry implements ISpecialInventory, IClimatised, IHintSource {

	public TileMachine() {
	}

	// / IERRORSOURCE
	@Override
	public boolean throwsErrors() {
		return false;
	}

	// / IHINTSOURCE
	@Override
	public boolean hasHints() {
		return false;
	}

	@Override
	public String[] getHints() {
		return null;
	}

	// / ICLIMATISED
	@Override
	public boolean isClimatized() {
		return false;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.NORMAL;
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.NORMAL;
	}

	@Override
	public float getExactTemperature() {
		return 0;
	}

	@Override
	public float getExactHumidity() {
		return 0;
	}

	// / IOWNABLE
	@Override
	public boolean isOwnable() {
		return true;
	}

	protected void createMachine() {
	}

	int oldkind;
	NBTTagCompound olddata;

	@Override
	public void initialize() {

		if (!Proxies.common.isSimulating(worldObj))
			return;

		if (olddata != null)
			legacyConversion(oldkind, olddata);

	}

	@Override
	public void updateEntity() {

		super.updateEntity();
	}

	@Override
	public void onRemoval() {
	}

	/**
	 * Read saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		int kind = nbttagcompound.getInteger("Kind");
		oldkind = kind;
		olddata = nbttagcompound;

	}

	private HashMap<Block, HashMap<Integer, MachineDefinition>> definitionMap;

	private void createDefinitionMap() {
		definitionMap = new HashMap<Block, HashMap<Integer, MachineDefinition>>();

		HashMap<Integer, MachineDefinition> machineMap = new HashMap<Integer, MachineDefinition>();
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_BOTTLER, PluginFactory.definitionBottler);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_CARPENTER, PluginFactory.definitionCarpenter);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_CENTRIFUGE, PluginFactory.definitionCentrifuge);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_FABRICATOR, PluginFactory.definitionFabricator);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_FERMENTER, PluginFactory.definitionFermenter);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_MOISTENER, PluginFactory.definitionMoistener);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_RAINTANK, PluginFactory.definitionRaintank);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_SQUEEZER, PluginFactory.definitionSqueezer);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_STILL, PluginFactory.definitionStill);
		machineMap.put(Defaults.ID_PACKAGE_MACHINE_APIARY, PluginApiculture.definitionApiary);
		machineMap.put(4, PluginEnergy.definitionGenerator);
		definitionMap.put(ForestryBlock.factoryTESR, machineMap);

		HashMap<Integer, MachineDefinition> millMap = new HashMap<Integer, MachineDefinition>();
		millMap.put(Defaults.ID_PACKAGE_MILL_MAILBOX, PluginMail.definitionMailbox);
		millMap.put(Defaults.ID_PACKAGE_MILL_TRADER, PluginMail.definitionTradestation);
		millMap.put(Defaults.ID_PACKAGE_MILL_PHILATELIST, PluginMail.definitionPhilatelist);
		millMap.put(Defaults.ID_PACKAGE_MILL_APIARIST_CHEST, PluginApiculture.definitionChest);
		millMap.put(Defaults.ID_PACKAGE_MILL_ANALYZER, PluginCore.definitionAnalyzer);
		millMap.put(Defaults.ID_PACKAGE_MILL_RAINMAKER, PluginFactory.definitionRainmaker);
		definitionMap.put(ForestryBlock.factoryPlain, millMap);

		HashMap<Integer, MachineDefinition> engineMap = new HashMap<Integer, MachineDefinition>();
		engineMap.put(0, PluginEnergy.definitionEngineBronze);
		engineMap.put(1, PluginEnergy.definitionEngineCopper);
		engineMap.put(2, PluginEnergy.definitionEngineTin);
		definitionMap.put(ForestryBlock.engine, engineMap);

	}

	private void legacyConversion(int kind, NBTTagCompound nbttagcompound) {
		if (definitionMap == null)
			createDefinitionMap();

		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (!definitionMap.containsKey(block) || !definitionMap.get(block).containsKey(kind)) {
			commitSeppuku(block, kind);
			return;
		}

		MachineDefinition definition = definitionMap.get(block).get(kind);
		Proxies.log.info("Converting obsolete gadget %s-%s to new '%s' %s-%s", block.getUnlocalizedName(), kind, definition.teIdent, definition.block.getUnlocalizedName(), definition.meta);

		Proxies.log.info("Removing old tile entity...");
		worldObj.removeTileEntity(xCoord, yCoord, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		Proxies.log.info("Setting to new block id...");
		worldObj.setBlock(xCoord, yCoord, zCoord, definition.block, definition.meta, Defaults.FLAG_BLOCK_SYNCH);
		TileEntity tile = worldObj.getTileEntity(xCoord, yCoord, zCoord);
		if (tile == null)
			throw new RuntimeException("Failed to set new block tile entity!");
		else if (tile.getClass() != definition.teClass)
			throw new RuntimeException(String.format("Converted tile entity was '%s' instead of expected '%s'", tile.getClass(), definition.teClass));
		Proxies.log.info("Refreshing converted tile entity %s with nbt data...", tile.getClass());
		if (nbttagcompound.hasKey("Machine"))
			tile.readFromNBT(complementNBT(nbttagcompound, nbttagcompound.getCompoundTag("Machine"), definition));
		else
			tile.readFromNBT(nbttagcompound);
	}

	private NBTTagCompound complementNBT(NBTTagCompound parent, NBTTagCompound inner, MachineDefinition definition) {

		inner.setString("id", definition.teIdent);
		inner.setInteger("x", this.xCoord);
		inner.setInteger("y", this.yCoord);
		inner.setInteger("z", this.zCoord);

		inner.setInteger("Access", parent.getInteger("Access"));
		if (parent.hasKey("Owner"))
			inner.setString("Owner", parent.getString("Owner"));
		if (parent.hasKey("Orientation"))
			inner.setInteger("Orientation", parent.getInteger("Orientation"));

		return inner;
	}

	private void commitSeppuku(Block block, int meta) {
		Proxies.log.info("Obsolete gadget %s-%s has no replacement defined. Committing sepukku.", block.getUnlocalizedName(), meta);
		worldObj.removeTileEntity(xCoord, yCoord, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

	/**
	 * Write save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		// Legacy for old fermenter with strange meta data
		int kind = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		nbttagcompound.setInteger("Kind", kind);

		/*
		 * if (machine != null) { NBTTagCompound NBTmachine = new NBTTagCompound(); machine.writeToNBT(NBTmachine); nbttagcompound.setTag("Machine",
		 * NBTmachine); } else { Proxies.log.warning("Saved a " + getClass() + " without machine."); }
		 */
	}

	@Override
	public void validate() {
		super.validate();
	}

	public boolean isWorking() {
		return false;
	}

	public int getChargeReceivedScaled(int i) {
		return 0;
	}

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5)
			return EnumTankLevel.EMPTY;
		else if (scaled < 30)
			return EnumTankLevel.LOW;
		else if (scaled < 60)
			return EnumTankLevel.MEDIUM;
		else if (scaled < 90)
			return EnumTankLevel.HIGH;
		else
			return EnumTankLevel.MAXIMUM;
	}

	// / REDSTONE SIGNALS
	public boolean isIndirectlyPoweringTo(IBlockAccess world, int i, int j, int k, int l) {
		return false;
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return false;
	}

	// IINVENTORY IMPLEMENTATION
	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
	}

	@Override
	public String getInventoryName() {
		return "[Unknown]";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}

	// IPARTICULARINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (getAccess() == EnumAccess.PRIVATE)
			return 0;

		return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		if (getAccess() == EnumAccess.PRIVATE)
			return new ItemStack[0];

		return new ItemStack[0];
	}

	// INETWORKEDTILE IMPLEMENTATION
	@Override
	public PacketPayload getPacketPayload() {

		PacketPayload payload = null;

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
	}

	// NEIGHBOUR CHANGE
	public void onNeighborBlockChange() {
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		return null;
	}

}

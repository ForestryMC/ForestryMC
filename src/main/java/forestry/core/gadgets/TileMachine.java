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
package forestry.core.gadgets;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IHintSource;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginEnergy;
import forestry.plugins.PluginFactory;
import forestry.plugins.PluginIC2;
import forestry.plugins.PluginMail;

public abstract class TileMachine extends TileForestry implements IClimatised, IHintSource {

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

	int oldkind;
	NBTTagCompound olddata;

	@Override
	public void initialize() {

		if (!Proxies.common.isSimulating(worldObj)) {
			return;
		}

		if (olddata != null) {
			legacyConversion(oldkind, olddata);
		}

	}

	/**
	 * Read saved data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		oldkind = nbttagcompound.getInteger("Kind");
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
		if (PluginIC2.instance.isAvailable()) {
			machineMap.put(Defaults.ID_PACKAGE_MACHINE_GENERATOR, PluginIC2.definitionGenerator);
		}
		definitionMap.put(ForestryBlock.factoryTESR.block(), machineMap);

		HashMap<Integer, MachineDefinition> millMap = new HashMap<Integer, MachineDefinition>();
		millMap.put(Defaults.ID_PACKAGE_MILL_MAILBOX, PluginMail.definitionMailbox);
		millMap.put(Defaults.ID_PACKAGE_MILL_TRADER, PluginMail.definitionTradestation);
		millMap.put(Defaults.ID_PACKAGE_MILL_PHILATELIST, PluginMail.definitionPhilatelist);
		millMap.put(Defaults.ID_PACKAGE_MILL_APIARIST_CHEST, PluginApiculture.definitionChest);
		millMap.put(Defaults.ID_PACKAGE_MILL_ANALYZER, PluginApiculture.definitionAnalyzer);
		millMap.put(Defaults.ID_PACKAGE_MILL_RAINMAKER, PluginFactory.definitionRainmaker);
		definitionMap.put(ForestryBlock.factoryPlain.block(), millMap);

		HashMap<Integer, MachineDefinition> engineMap = new HashMap<Integer, MachineDefinition>();
		engineMap.put(0, PluginEnergy.definitionEngineBronze);
		engineMap.put(1, PluginEnergy.definitionEngineCopper);
		if (PluginIC2.instance.isAvailable()) {
			engineMap.put(2, PluginIC2.definitionEngineTin);
		}
		definitionMap.put(ForestryBlock.engine.block(), engineMap);

	}

	private void legacyConversion(int kind, NBTTagCompound nbttagcompound) {
		if (definitionMap == null) {
			createDefinitionMap();
		}

		Block block = worldObj.getBlockState(pos).getBlock();
		if (!definitionMap.containsKey(block) || !definitionMap.get(block).containsKey(kind)) {
			commitSeppuku(block, kind);
			return;
		}

		MachineDefinition definition = definitionMap.get(block).get(kind);
		Proxies.log.info("Converting obsolete gadget %s-%s to new '%s' %s-%s", block.getUnlocalizedName(), kind, definition.teIdent, definition.block.getUnlocalizedName(), definition.meta);

		Proxies.log.info("Removing old tile entity...");
		worldObj.removeTileEntity(pos);
		worldObj.setBlockToAir(pos);
		Proxies.log.info("Setting to new block id...");
		worldObj.setBlockState(pos, definition.block.getStateFromMeta(definition.meta), Defaults.FLAG_BLOCK_SYNCH);
		TileEntity tile = worldObj.getTileEntity(pos);
		if (tile == null) {
			throw new RuntimeException("Failed to set new block tile entity!");
		} else if (tile.getClass() != definition.teClass) {
			throw new RuntimeException(String.format("Converted tile entity was '%s' instead of expected '%s'", tile.getClass(), definition.teClass));
		}
		Proxies.log.info("Refreshing converted tile entity %s with nbt data...", tile.getClass());
		if (nbttagcompound.hasKey("Machine")) {
			tile.readFromNBT(complementNBT(nbttagcompound, nbttagcompound.getCompoundTag("Machine"), definition));
		} else {
			tile.readFromNBT(nbttagcompound);
		}
	}

	private NBTTagCompound complementNBT(NBTTagCompound parent, NBTTagCompound inner, MachineDefinition definition) {

		inner.setString("id", definition.teIdent);
		inner.setInteger("x", pos.getX());
		inner.setInteger("y", pos.getY());
		inner.setInteger("z", pos.getZ());

		inner.setInteger("Access", parent.getInteger("Access"));
		if (parent.hasKey("Owner")) {
			inner.setString("Owner", parent.getString("Owner"));
		}
		if (parent.hasKey("Orientation")) {
			inner.setInteger("Orientation", parent.getInteger("Orientation"));
		}

		return inner;
	}

	private void commitSeppuku(Block block, int meta) {
		Proxies.log.info("Obsolete gadget %s-%s has no replacement defined. Committing sepukku.", block.getUnlocalizedName(), meta);
		worldObj.removeTileEntity(pos);
		worldObj.setBlockToAir(pos);
	}

	/**
	 * Write save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		// Legacy for old fermenter with strange meta data
		IBlockState state = worldObj.getBlockState(pos);
		int kind = state.getBlock().getMetaFromState(state);
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

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5) {
			return EnumTankLevel.EMPTY;
		} else if (scaled < 30) {
			return EnumTankLevel.LOW;
		} else if (scaled < 60) {
			return EnumTankLevel.MEDIUM;
		} else if (scaled < 90) {
			return EnumTankLevel.HIGH;
		} else {
			return EnumTankLevel.MAXIMUM;
		}
	}

	// INETWORKEDTILE IMPLEMENTATION
	@Override
	public PacketPayload getPacketPayload() {
		return null;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
	}
	
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}

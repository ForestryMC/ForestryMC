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
package forestry.greenhouse.tiles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.climate.ClimateSource;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.greenhouse.GreenhouseClimateSource;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class TileGreenhouseClimatiser extends TileGreenhouse implements IActivatable, IGreenhouseComponent.Climatiser {

	protected static final int WORK_CYCLES = 1;
	protected static final int ENERGY_PER_OPERATION = 150;

	private final IClimatiserDefinition definition;
	private final ClimateSource source;

	protected final Set<BlockPos> positionsInRange;

	private boolean active;

	protected TileGreenhouseClimatiser(IClimatiserDefinition definition, int ticksForChange) {
		this(definition, new GreenhouseClimateSource(ticksForChange));
	}

	protected TileGreenhouseClimatiser(IClimatiserDefinition definition, ClimateSource source) {
		this.definition = definition;
		this.source = source;
		this.source.setProvider(this);
		this.positionsInRange = new HashSet();
	}

	protected TileGreenhouseClimatiser(IClimatiserDefinition definition) {
		this(definition, 20 + ENERGY_PER_OPERATION / 25);
	}

	@Override
	public void onMachineBroken() {
		positionsInRange.clear();
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		positionsInRange.clear();
		float range = definition.getRange();

		Iterable<BlockPos> allInBox = BlockPos.getAllInBox(pos.add(-range, -range, -range), pos.add(range, range, range));
		for (BlockPos pos : allInBox) {
			if (pos.distanceSq(this.pos) <= range) {
				positionsInRange.add(pos);
			}
		}
	}

	/* Network */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.setBoolean("Active", active);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		setActive(packetData.getBoolean("Active"));
	}

	/* IActivatable */
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public IClimatiserDefinition getDefinition() {
		return definition;
	}

	@Override
	public IClimateSource getClimateSource() {
		return source;
	}

	public boolean canWork() {
		MultiblockLogicGreenhouse logic = getMultiblockLogic();
		if (!logic.isConnected()) {
			return false;
		}

		EnergyManager energyManager = logic.getController().getEnergyManager();

		return EnergyHelper.consumeEnergyToDoWork(energyManager, WORK_CYCLES, ENERGY_PER_OPERATION);
	}

	@Override
	public Set<BlockPos> getPositionsInRange() {
		return positionsInRange;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (world != null) {
			if (world.isRemote) {
				world.markBlockRangeForRenderUpdate(getCoordinates(), getCoordinates());
			} else {
				Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), getCoordinates(), world);
			}
		}
	}

}

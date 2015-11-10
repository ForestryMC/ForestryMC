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
package forestry.apiculture.multiblock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ITitled;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary implements IClimatised, IHintSource, ITitled, ITriggerProvider, IStreamableGui {

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		if (!worldObj.isRemote) {
			// set alveary entrance block meta
			if (yCoord == maxCoord.posY) {
				if ((xCoord > minCoord.posX && xCoord < maxCoord.posX) || (zCoord > minCoord.posZ && zCoord < maxCoord.posZ)) {
					this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, TileAlveary.ENTRANCE_META, 2);
				}
			}
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		if (!worldObj.isRemote) {
			// set alveary entrance block meta back to normal
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, TileAlveary.PLAIN_META, 2);
		}
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.for.alveary.0.name";
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("apiary");
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		return null;
	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		Collection<ITriggerExternal> res = new ArrayList<>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		return res;
	}

	/* IClimatised */
	@Override
	public float getExactTemperature() {
		return getMultiblockLogic().getController().getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return getMultiblockLogic().getController().getExactHumidity();
	}
}

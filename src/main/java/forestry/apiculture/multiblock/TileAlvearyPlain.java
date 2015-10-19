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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.rectangular.PartPosition;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ITitled;
import forestry.core.utils.BlockUtil;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary implements IClimatised, IHintSource, ITitled, ITriggerProvider, IStreamableGui {

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);

		if (!worldObj.isRemote) {
			// set alveary entrance block meta
			if (getPartPosition() == PartPosition.Frame) {
				if (BlockUtil.isWoodSlabBlock(worldObj.getBlock(xCoord, yCoord + 1, zCoord))) {
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
		return getAlvearyController().getInternalInventory();
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		if (isConnected()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		}
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getAlvearyController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getAlvearyController().readGuiData(data);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.for.alveary.0.name";
	}

	/* IHintSource */
	@Override
	public String[] getHints() {
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
		return getAlvearyController().getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return getAlvearyController().getExactHumidity();
	}
}

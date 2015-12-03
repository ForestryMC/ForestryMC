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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.trigger.ApicultureTriggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary implements ITriggerProvider {

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		if (!worldObj.isRemote) {
			// set alveary entrance block meta
			if (yCoord == maxCoord.posY) {
				if ((xCoord > minCoord.posX && xCoord < maxCoord.posX) || (zCoord > minCoord.posZ && zCoord < maxCoord.posZ)) {
					this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, BlockAlveary.Type.ENTRANCE.ordinal(), 2);
				}
			}
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		if (!worldObj.isRemote) {
			// set alveary entrance block meta back to normal
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, BlockAlveary.Type.PLAIN.ordinal(), 2);
		}
	}

	@Override
	public boolean allowsAutomation() {
		return true;
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
}

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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
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
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		super.onMachineAssembled(multiblockController, minCoord, maxCoord);

		if (!worldObj.isRemote) {
			// set alveary entrance block meta
			if (getPos().getY() == maxCoord.getY()) {
				if ((getPos().getX() > minCoord.getX() && getPos().getX() < maxCoord.getX()) || (getPos().getZ() > minCoord.getZ() && getPos().getZ() < maxCoord.getZ())) {
					this.worldObj.setBlockState(getPos(), getBlockType().getStateFromMeta(BlockAlveary.AlvearyType.ENTRANCE.ordinal()), 2);
				}
			}
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		if (!worldObj.isRemote) {
			// set alveary entrance block meta back to normal
			this.worldObj.setBlockState(getPos(), getBlockType().getStateFromMeta(BlockAlveary.AlvearyType.PLAIN.ordinal()), 2);
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
	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
		Collection<ITriggerExternal> res = new ArrayList<>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		return res;
	}
}

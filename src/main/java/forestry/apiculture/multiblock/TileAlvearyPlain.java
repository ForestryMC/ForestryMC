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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

import java.util.Collection;

import javax.annotation.Nonnull;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerInternalSided;
import buildcraft.api.statements.ITriggerProvider;
import forestry.apiculture.trigger.ApicultureTriggers;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary implements ITriggerProvider {

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public void addInternalTriggers(Collection<ITriggerInternal> triggers, IStatementContainer container) { }

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public void addInternalSidedTriggers(Collection<ITriggerInternalSided> triggers, IStatementContainer container, @Nonnull EnumFacing side) { }

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull EnumFacing side, TileEntity tile) {
		triggers.add(ApicultureTriggers.missingQueen);
		triggers.add(ApicultureTriggers.missingDrone);
	}
}

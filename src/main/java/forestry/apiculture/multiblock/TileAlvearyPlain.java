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

//@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary {//implements ITriggerProvider {

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* ITRIGGERPROVIDER */
	// TODO: buildcraft for 1.9
	//	@Optional.Method(modid = "BuildCraftAPI|statements")
	//	@Override
	//	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
	//		return null;
	//	}
	//
	//	@Optional.Method(modid = "BuildCraftAPI|statements")
	//	@Override
	//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
	//		Collection<ITriggerExternal> res = new ArrayList<>();
	//		res.add(ApicultureTriggers.missingQueen);
	//		res.add(ApicultureTriggers.missingDrone);
	//		return res;
	//	}
}

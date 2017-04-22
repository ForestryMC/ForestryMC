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
package forestry.plugins.compat;

import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraftforge.fml.common.Optional;

@ForestryPlugin(pluginID = ForestryPluginUids.BUILDCRAFT_STATEMENTS, name = "BuildCraft 6 Statements", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
//@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class PluginBuildCraftStatements extends BlankForestryPlugin {//implements ITriggerProvider {

	@Override
	public boolean isAvailable() {
		return ModUtil.isAPILoaded("buildcraft.api.statements", "[1.0, 2.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|statements version not found";
	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public void doInit() {
		// Add custom trigger handler
		// TODO: Buildcraft for 1.9
//		StatementManager.registerTriggerProvider(this);
	}

	/* ITriggerProvider */

	// TODO: Buildcraft for 1.9
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
//		TileEntity tile = container.getTile();
//		if (tile instanceof ITriggerProvider) {
//			return ((ITriggerProvider) tile).getInternalTriggers(container);
//		}
//
//		return null;
//	}
//
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
//		if (tile instanceof ITriggerProvider) {
//			return ((ITriggerProvider) tile).getExternalTriggers(side, tile);
//		}
//
//		return null;
//	}
}

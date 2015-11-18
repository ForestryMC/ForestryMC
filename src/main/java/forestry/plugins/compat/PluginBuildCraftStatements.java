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

import java.util.Collection;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.statements.StatementManager;

@Plugin(pluginID = "BC6|Statements", name = "BuildCraft 6 Statements", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class PluginBuildCraftStatements extends ForestryPlugin implements ITriggerProvider {

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
		StatementManager.registerTriggerProvider(this);
	}

	/* ITriggerProvider */

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		TileEntity tile = container.getTile();
		if (tile instanceof ITriggerProvider) {
			return ((ITriggerProvider) tile).getInternalTriggers(container);
		}

		return null;
	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		if (tile instanceof ITriggerProvider) {
			return ((ITriggerProvider) tile).getExternalTriggers(side, tile);
		}

		return null;
	}
}

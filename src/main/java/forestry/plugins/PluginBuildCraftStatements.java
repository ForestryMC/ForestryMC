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
package forestry.plugins;

import javax.annotation.Nonnull;
import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.common.Optional;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerInternalSided;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.statements.StatementManager;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BUILDCRAFT_STATEMENTS, name = "BuildCraft 6 Statements", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.buildcraft6.description")
@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = Constants.BCLIB_MOD_ID)
public class PluginBuildCraftStatements extends BlankForestryModule implements ITriggerProvider {

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(Constants.BCLIB_MOD_ID, "[7.99.17,8.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|statements version not found";
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void doInit() {
		// Add custom trigger handler
		StatementManager.registerTriggerProvider(this);
	}

	/* ITriggerProvider */

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void addInternalTriggers(Collection<ITriggerInternal> triggers, IStatementContainer container) {
		TileEntity tile = container.getTile();
		if (tile instanceof ITriggerProvider) {
			((ITriggerProvider) tile).addInternalTriggers(triggers, container);
		}
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void addInternalSidedTriggers(Collection<ITriggerInternalSided> triggers, IStatementContainer container, @Nonnull EnumFacing side) {
		TileEntity tile = container.getTile();
		if (tile instanceof ITriggerProvider) {
			((ITriggerProvider) tile).addInternalSidedTriggers(triggers, container, side);
		}
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void addExternalTriggers(Collection<ITriggerExternal> triggers, @Nonnull EnumFacing side, TileEntity tile) {
		if (tile instanceof ITriggerProvider) {
			((ITriggerProvider) tile).addExternalTriggers(triggers, side, tile);
		}
	}
}

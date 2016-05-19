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
package forestry.core.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileForestry;

public enum BlockTypeCoreTesr implements IBlockTypeTesr {
	ANALYZER(TileAnalyzer.class, "analyzer", Proxies.render.getRendererAnalyzer()),
	ESCRITOIRE(TileEscritoire.class, "escritoire", Proxies.render.getRenderEscritoire());

	public static final BlockTypeCoreTesr[] VALUES = values();

	@Nonnull
	private final IMachinePropertiesTesr machineProperties;

	<T extends TileForestry> BlockTypeCoreTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<T> renderer) {
		this.machineProperties = new MachinePropertiesTesr<>(teClass, name, renderer, Constants.RESOURCE_ID + ":blocks/" + name + ".0");
	}

	@Nonnull
	@Override
	public IMachinePropertiesTesr getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}

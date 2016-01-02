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

import java.util.Locale;

import javax.annotation.Nullable;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileForestry;
import forestry.plugins.PluginApiculture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public enum BlockCoreType implements IMachinePropertiesTESR {
	ANALYZER(TileAnalyzer.class, "Analyzer") {
		@Nullable
		@Override
		public TileEntitySpecialRenderer getRenderer() {
			return PluginApiculture.proxy.getRendererAnalyzer(Constants.TEXTURE_PATH_BLOCKS + "/analyzer_");
		}
	},
	ESCRITOIRE(TileEscritoire.class, "Escritoire") {
		@Nullable
		@Override
		public TileEntitySpecialRenderer getRenderer() {
			return Proxies.render.getRenderEscritoire();
		}
	};

	public static final BlockCoreType[] VALUES = values();

	private final String teIdent;
	private final Class<? extends TileForestry> teClass;

	BlockCoreType(Class<? extends TileForestry> teClass, String teName) {
		this.teIdent = "forestry." + teName;
		this.teClass = teClass;
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	@Override
	public String getTeIdent() {
		return teIdent;
	}

	@Override
	public Class<? extends TileForestry> getTeClass() {
		return teClass;
	}
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}

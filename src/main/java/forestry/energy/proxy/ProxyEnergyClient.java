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
package forestry.energy.proxy;

import forestry.core.tiles.TileEngine;
import forestry.energy.render.RenderEngine;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SuppressWarnings("unused")
public class ProxyEnergyClient extends ProxyEnergy {

	@Override
	public TileEntitySpecialRenderer<TileEngine> getRenderDefaultEngine(String gfxBase) {
		return new RenderEngine(gfxBase);
	}

}

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
package forestry.core;

public class ForestryClient extends ForestryCore {

	public static int byBlockModelId;
	public static int candleRenderId;

	@Override
	public void init(Object basemod) {

		super.init(basemod);

		// byBlockModelId = Proxies.render.getNextAvailableRenderId();
		// candleRenderId = Proxies.render.getNextAvailableRenderId();

		// BlockRenderingHandler renderHandler = new BlockRenderingHandler();
		// RenderingRegistry.registerBlockHandler(byBlockModelId,
		// renderHandler);
	}

	@Override
	public void postInit() {
		super.postInit();
	}

}

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
package forestry.storage.proxy;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.core.items.ItemCrated;
import forestry.core.render.RenderCrateItem;

public class ProxyStorageClient extends ProxyStorage {

	private static final RenderCrateItem crateRenderer = new RenderCrateItem();

	@Override
	public void registerCrateForRendering(ItemCrated crate) {
		MinecraftForgeClient.registerItemRenderer(crate, crateRenderer);
	}
}

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
package forestry.book.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookManager;
import forestry.book.BookLoader;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyBookClient extends ProxyBook {

	@Override
	public void setupAPI() {
		BookManager.loader = BookLoader.INSTANCE;
	}

	@Override
	public void preInit() {
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager instanceof IReloadableResourceManager) {
			IReloadableResourceManager manager = (IReloadableResourceManager) resourceManager;
			manager.addReloadListener(BookLoader.INSTANCE);
		}
	}
}

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
package forestry.climatology.proxy;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.climatology.PreviewHandlerClient;

@SideOnly(Side.CLIENT)
public class ProxyClimatologyClient extends ProxyClimatology {

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new PreviewHandlerClient());
	}

}

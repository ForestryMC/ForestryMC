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
package forestry.core.proxy;

import cpw.mods.fml.common.SidedProxy;

public class Proxies {
	@SidedProxy(clientSide = "forestry.core.proxy.ProxyCommonClient", serverSide = "forestry.core.proxy.ProxyCommon")
	public static ProxyCommon common;
	@SidedProxy(clientSide = "forestry.core.proxy.ProxyNetworkClient", serverSide = "forestry.core.proxy.ProxyNetwork")
	public static ProxyNetwork net;
	@SidedProxy(clientSide = "forestry.core.proxy.ProxyRenderClient", serverSide = "forestry.core.proxy.ProxyRender")
	public static ProxyRender render;
}

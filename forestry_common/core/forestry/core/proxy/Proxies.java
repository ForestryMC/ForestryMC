/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.proxy;

import cpw.mods.fml.common.SidedProxy;

public class Proxies {
	@SidedProxy(clientSide = "forestry.core.proxy.ClientProxyCommon", serverSide = "forestry.core.proxy.ProxyCommon")
	public static ProxyCommon common;
	@SidedProxy(clientSide = "forestry.core.proxy.ClientProxyNetwork", serverSide = "forestry.core.proxy.ProxyNetwork")
	public static ProxyNetwork net;
	@SidedProxy(clientSide = "forestry.core.proxy.ClientProxyRender", serverSide = "forestry.core.proxy.ProxyRender")
	public static ProxyRender render;

	public static ProxyLog log = new ProxyLog();
}

/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.proxy;

//import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;

//TODO - DistExecutor
public class Proxies {
    //	@SidedProxy(clientSide = "forestry.core.proxy.ProxyClient", serverSide = "forestry.core.proxy.ProxyCommon")
    @Nullable
    public static ProxyCommon common;

    //	@SidedProxy(clientSide = "forestry.core.proxy.ProxyRenderClient", serverSide = "forestry.core.proxy.ProxyRender")
    @Nullable
    public static ProxyRender render;
}

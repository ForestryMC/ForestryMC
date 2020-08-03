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

import forestry.climatology.PreviewHandlerClient;
import forestry.climatology.features.ClimatologyBlocks;
import forestry.modules.IClientModuleHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ProxyClimatologyClient extends ProxyClimatology implements IClientModuleHandler {

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new PreviewHandlerClient());
    }

    @Override
    public void setupClient(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(ClimatologyBlocks.HABITATFORMER.block(), RenderType.getCutout());
    }

}

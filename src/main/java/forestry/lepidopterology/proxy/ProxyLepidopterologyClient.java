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
package forestry.lepidopterology.proxy;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.core.config.Constants;
import forestry.lepidopterology.features.LepidopterologyEntities;
import forestry.lepidopterology.render.ModelButterflyLoader;
import forestry.lepidopterology.render.RenderButterflyEntity;
import forestry.modules.IClientModuleHandler;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyLepidopterologyClient extends ProxyLepidopterology implements IClientModuleHandler {

	@Override
	public void setupClient(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(LepidopterologyEntities.BUTTERFLY.entityType(), RenderButterflyEntity::new);
		ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "butterfly_ge"), new ModelButterflyLoader());
	}
}

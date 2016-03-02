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

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import forestry.core.models.ModelIndex;
import forestry.core.models.ModelManager;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.render.ModelButterflyItem;
import forestry.lepidopterology.render.RenderButterflyEntity;

public class ProxyLepidopterologyClient extends ProxyLepidopterology {

	@Override
	public void initializeRendering() {
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, new RenderButterflyEntity());
		Proxies.render.registerModel(new ModelIndex(ModelManager.getInstance().getModelLocation("butterflyGE"), new ModelButterflyItem()));
	}
}

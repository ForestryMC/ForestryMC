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

import com.google.common.collect.ImmutableList;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.config.Constants;
import forestry.lepidopterology.features.LepidopterologyEntities;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.render.ButterflyEntityRenderer;
import forestry.lepidopterology.render.ButterflyItemModel;
import forestry.lepidopterology.render.CocoonItemModel;
import forestry.modules.IClientModuleHandler;
import genetics.utils.AlleleUtils;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyLepidopterologyClient extends ProxyLepidopterology implements IClientModuleHandler {

    @Override
    public void registerModels(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(LepidopterologyEntities.BUTTERFLY.entityType(), ButterflyEntityRenderer::new);
        ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "butterfly_ge"), new ButterflyItemModel.Loader());
        ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "butterfly_cocoon"), new CocoonItemModel.Loader());
        AlleleUtils.forEach(ButterflyChromosomes.COCOON, (allele) -> {
            ImmutableList.Builder<IBakedModel> models = new ImmutableList.Builder<>();
            for (int age = 0; age < ItemButterflyGE.MAX_AGE; age++) {
                ModelLoader.addSpecialModel(allele.getCocoonItemModel(age));
            }
        });
    }
}

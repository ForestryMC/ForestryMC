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
package forestry.arboriculture.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.FoliageColors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.models.ModelDecorativeLeaves;
import forestry.arboriculture.models.ModelDefaultLeaves;
import forestry.arboriculture.models.ModelDefaultLeavesFruit;
import forestry.arboriculture.models.ModelLeaves;
import forestry.arboriculture.models.ModelSapling;
import forestry.core.models.ClientManager;
import forestry.core.models.DefaultTextureGetter;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

@OnlyIn(Dist.CLIENT)
public class ProxyArboricultureClient extends ProxyArboriculture {

	@Override
	public void initializeModels() {
		ClientManager clientManager = ClientManager.getInstance();
		clientManager.registerModel(new ModelLeaves(), ArboricultureBlocks.LEAVES);
		clientManager.registerModel(new ModelDecorativeLeaves(), ArboricultureBlocks.LEAVES_DECORATIVE);
		clientManager.registerModel(new ModelDefaultLeaves(), ArboricultureBlocks.LEAVES_DEFAULT);
		clientManager.registerModel(new ModelDefaultLeavesFruit(), ArboricultureBlocks.LEAVES_DEFAULT_FRUIT);
	}

	public void onModelRegister() {
		for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.SPECIES)) {
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) allele;
				ModelLoader.addSpecialModel(treeSpecies.getBlockModel());
				ModelLoader.addSpecialModel(treeSpecies.getItemModel());
			}
		}
		//ModelLoader.addSpecialModel();
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		//TODO: Remove if forge fixes the model loaders
		IBakedModel model = new ModelSapling().bake(event.getModelLoader(), DefaultTextureGetter.INSTANCE, ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK);
		for (BlockState state : ArboricultureBlocks.SAPLING_GE.block().getStateContainer().getValidStates()) {
			event.getModelRegistry().put(BlockModelShapes.getModelLocation(state), model);
		}
		event.getModelRegistry().put(new ModelResourceLocation("forestry:sapling", "inventory"), new ModelSapling().bake(event.getModelLoader(), DefaultTextureGetter.INSTANCE, ModelRotation.X0_Y0, DefaultVertexFormats.ITEM));
	}

	@Override
	public int getFoliageColorDefault() {
		return FoliageColors.getDefault();
	}

	@Override
	public int getFoliageColorBirch() {
		return FoliageColors.getBirch();
	}

	@Override
	public int getFoliageColorSpruce() {
		return FoliageColors.getSpruce();
	}
}

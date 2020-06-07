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

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import genetics.utils.AlleleUtils;

import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.models.ModelDecorativeLeaves;
import forestry.arboriculture.models.ModelDefaultLeaves;
import forestry.arboriculture.models.ModelDefaultLeavesFruit;
import forestry.arboriculture.models.ModelLeaves;
import forestry.arboriculture.models.SaplingModelLoader;
import forestry.arboriculture.models.TextureLeaves;
import forestry.core.config.Constants;
import forestry.core.models.ClientManager;
import forestry.modules.IClientModuleHandler;

@OnlyIn(Dist.CLIENT)
public class ProxyArboricultureClient extends ProxyArboriculture implements IClientModuleHandler {

	@Override
	public void initializeModels() {
		ClientManager clientManager = ClientManager.getInstance();
		clientManager.registerModel(new ModelLeaves(), ArboricultureBlocks.LEAVES);
		clientManager.registerModel(new ModelDecorativeLeaves(), ArboricultureBlocks.LEAVES_DECORATIVE);
		clientManager.registerModel(new ModelDefaultLeaves(), ArboricultureBlocks.LEAVES_DEFAULT);
		clientManager.registerModel(new ModelDefaultLeavesFruit(), ArboricultureBlocks.LEAVES_DEFAULT_FRUIT);
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

	@Override
	public void registerSprites(TextureStitchEvent.Pre event) {
		if (event.getMap().getTextureLocation() != PlayerContainer.LOCATION_BLOCKS_TEXTURE) {
			return;
		}
		TextureLeaves.registerAllSprites(event);
		for (IAlleleFruit alleleFruit : AlleleFruits.getFruitAlleles()) {
			alleleFruit.getProvider().registerSprites(event);
		}
	}

	@Override
	public void bakeModels(ModelBakeEvent event) {
		//TODO: Remove if forge fixes the model loaders
		/*IBakedModel model = new ModelSapling().bake(event.getModelLoader(), DefaultTextureGetter.INSTANCE, ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK);
		for (BlockState state : ArboricultureBlocks.SAPLING_GE.block().getStateContainer().getValidStates()) {
			event.getModelRegistry().put(BlockModelShapes.getModelLocation(state), model);
		}
		event.getModelRegistry().put(new ModelResourceLocation("forestry:sapling", "inventory"), new ModelSapling().bake(event.getModelLoader(), DefaultTextureGetter.INSTANCE, ModelRotation.X0_Y0, DefaultVertexFormats.ITEM));*/
	}

	@Override
	public void registerModels(ModelRegistryEvent event) {
		AlleleUtils.forEach(TreeChromosomes.SPECIES, (treeSpecies) -> {
			ModelLoader.addSpecialModel(treeSpecies.getBlockModel());
			ModelLoader.addSpecialModel(treeSpecies.getItemModel());
		});
		//ModelLoader.addSpecialModel();
	}

	@Override
	public void setupClient(FMLClientSetupEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "sapling_ge"), SaplingModelLoader.INSTANCE);
		ArboricultureBlocks.TREE_CHEST.block().clientSetup();

		// fruit overlays require CUTOUT_MIPPED, even in Fast graphics
		ArboricultureBlocks.LEAVES_DEFAULT.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
		RenderTypeLookup.setRenderLayer(ArboricultureBlocks.LEAVES.block(), RenderType.getCutoutMipped());
		ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
		ArboricultureBlocks.LEAVES_DECORATIVE.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
		RenderTypeLookup.setRenderLayer(ArboricultureBlocks.SAPLING_GE.block(), RenderType.getCutout());
		ArboricultureBlocks.DOORS.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getTranslucent()));
	}
}

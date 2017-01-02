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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import forestry.api.arboriculture.IWoodItemMeshDefinition;
import forestry.api.arboriculture.IWoodStateMapper;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockArbSlab;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.models.ModelDecorativeLeaves;
import forestry.arboriculture.models.ModelLeaves;
import forestry.arboriculture.models.WoodModelLoader;
import forestry.arboriculture.models.WoodTextures;
import forestry.core.models.BlockModelEntry;
import forestry.core.models.SimpleRetexturedModel;
import forestry.core.models.WoodModelEntry;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.ColorizerFoliage;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProxyArboricultureClient extends ProxyArboriculture {
	private static final Set<WoodModelEntry> woodModelEntrys = new HashSet<>();

	@Override
	public void initializeModels() {
		{
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:leaves");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:leaves", "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelLeaves(), PluginArboriculture.getBlocks().leaves);
			Proxies.render.registerBlockModel(blockModelIndex);
		}

		for (BlockDecorativeLeaves leaves : PluginArboriculture.getBlocks().leavesDecorative) {
			String resourceName = "forestry:leaves.decorative." + leaves.getBlockNumber();
			ModelResourceLocation blockModelLocation = new ModelResourceLocation(resourceName);
			ModelResourceLocation itemModeLocation = new ModelResourceLocation(resourceName, "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModeLocation, new ModelDecorativeLeaves(), leaves);
			Proxies.render.registerBlockModel(blockModelIndex);
		}

		ModelLoaderRegistry.registerLoader(WoodModelLoader.INSTANCE);
		for (BlockArbSlab slab : PluginArboriculture.getBlocks().slabsDouble) {
			registerWoodModel(slab, true);
		}
		for (BlockArbSlab slab : PluginArboriculture.getBlocks().slabsDoubleFireproof) {
			registerWoodModel(slab, true);
		}
	}

	@SubscribeEvent
	public <T extends Block & IWoodTyped> void onModelBake(ModelBakeEvent event) {
		WoodModelLoader.INSTANCE.isRegistered = true;
		IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
		Minecraft minecraft = Proxies.common.getClientInstance();
		Map<ResourceLocation, Exception> loadingExceptions = ObfuscationReflectionHelper.getPrivateValue(ModelLoader.class, event.getModelLoader(), 2);

		if (minecraft.getBlockRendererDispatcher() != null) {
			BlockStateMapper stateMapper = minecraft.getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper();
			Map<Item, ItemMeshDefinition> definitions = ObfuscationReflectionHelper.getPrivateValue(ItemModelMesher.class, minecraft.getRenderItem().getItemModelMesher(), 2);
			Map<Block, IStateMapper> blockStateMap = ObfuscationReflectionHelper.getPrivateValue(BlockStateMapper.class, stateMapper, 0);

			for (WoodModelEntry<T> entry : woodModelEntrys) {
				T woodTyped = entry.woodTyped;
				WoodBlockKind woodKind = woodTyped.getBlockKind();

				IStateMapper mapper = blockStateMap.get(woodTyped);
				if (mapper instanceof IWoodStateMapper) {
					IWoodStateMapper woodMapper = (IWoodStateMapper) mapper;
					try {
						for (IBlockState state : woodTyped.getBlockState().getValidStates()) {
							IWoodType woodType;
							ItemStack itemStack;
							if (entry.withVariants) {
								int meta = woodTyped.getMetaFromState(state);
								woodType = woodTyped.getWoodType(meta);
								itemStack = new ItemStack(woodTyped, 1, meta);
							} else {
								woodType = woodTyped.getWoodType(0);
								itemStack = new ItemStack(woodTyped);
							}
							ImmutableMap<String, String> customTextures = WoodTextures.getLocations(woodType, woodKind);
							if (woodKind.retextureItem) {
								ItemMeshDefinition definition = definitions.get(itemStack.getItem());
								if (definition instanceof IWoodItemMeshDefinition) {
									IWoodItemMeshDefinition woodDefinition = (IWoodItemMeshDefinition) definition;
									IModel basicItemModel = ModelLoaderRegistry.getModel(woodDefinition.getDefaultModelLocation(itemStack));
									ModelResourceLocation basicItemLocation = definition.getModelLocation(itemStack);

									registry.putObject(basicItemLocation, new SimpleRetexturedModel(woodKind.retextureModel(basicItemModel, woodType, customTextures)));
								}
							}
							IModel basicModel = ModelLoaderRegistry.getModel(woodMapper.getDefaultModelResourceLocation(state));
							ModelResourceLocation basicLocation = woodMapper.getModelLocation(state);
							if (loadingExceptions.containsKey(basicLocation)) {
								loadingExceptions.remove(basicLocation);
							}
							registry.putObject(basicLocation, new SimpleRetexturedModel(woodKind.retextureModel(basicModel, woodType, customTextures)));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		loadingExceptions.putAll(WoodModelLoader.loadingExceptions);
	}

	@Override
	public <T extends Block & IWoodTyped> void registerWoodModel(T woodTyped, boolean withVariants) {
		woodModelEntrys.add(new WoodModelEntry<>(woodTyped, withVariants));
	}

	@Override
	public int getFoliageColorBasic() {
		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	public int getFoliageColorBirch() {
		return ColorizerFoliage.getFoliageColorBirch();
	}

	@Override
	public int getFoliageColorPine() {
		return ColorizerFoliage.getFoliageColorPine();
	}
}

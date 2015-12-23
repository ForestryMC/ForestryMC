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
package forestry.core.render.model;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.core.IModelBaker;
import forestry.api.core.IStateMapperRegister;
import forestry.core.utils.StringUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.PluginManager;
import forestry.plugins.PluginManager.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelManager implements IModelManager {

	private static ModelManager instance;

	public static ModelManager getInstance() {
		if (instance == null) {
			instance = new ModelManager();
			ForestryAPI.modleManager = instance;
		}

		return instance;
	}

	@Override
	public void registerItemModel(Item item, int meta, String identifier) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
				getModelLocation(item, identifier));
		registerVariant(item, "forestry:" + identifier);
	}

	@Override
	public void registerItemModel(Item item, int meta, String modID, String identifier) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item, modID,  identifier));
		registerVariant(item, modID + ":" + identifier);
	}

	@Override
	public void registerItemModel(Item item, int meta, boolean withMeta) {
		if(withMeta){
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item, meta));
			registerVariant(item, "forestry:" + StringUtil.cleanItemName(new ItemStack(item, 1, meta)));
		}else{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getModelLocation(item));
			registerVariant(item, "forestry:" + StringUtil.cleanItemName(item));
		}
	}

	@Override
	public void registerItemModel(Item item, ItemMeshDefinition definition) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, definition);
	}

	@Override
	public void registerVariant(Item item, String... names) {
		ModelBakery.addVariantName(item, names);
	}

	public static void registerModels() {
		for(Module module : PluginManager.getLoadedModules()){
			ForestryPlugin plugin = module.instance();
			
			for (Block block : GameData.getBlockRegistry()) {
				if (block != null) {
					if (block instanceof IModelRegister) {
						((IModelRegister) block).registerModel(Item.getItemFromBlock(block), getInstance());
					}
					if (block instanceof IStateMapperRegister) {
						((IStateMapperRegister) block).registerStateMapper();
					}
				}

			}
			for (Item item : GameData.getItemRegistry()) {
				if (item != null) {
					if (item instanceof IModelRegister) {
						((IModelRegister) item).registerModel(item, getInstance());
					}
				}
			}
		}
	}
	
	public static void registerStateMappers(){
		for(Module module : PluginManager.getLoadedModules()){
			ForestryPlugin plugin = module.instance();
			
			for (Block block : GameData.getBlockRegistry()) {
				if (block != null) {
					if (block instanceof IStateMapperRegister) {
						((IStateMapperRegister) block).registerStateMapper();
					}
				}

			}
		}
	}

	@Override
	public ModelResourceLocation getModelLocation(String identifier) {
		return new ModelResourceLocation("forestry:" + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(String modID, String identifier) {
		return new ModelResourceLocation(modID + ":" + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta) {
		return new ModelResourceLocation(
				"forestry:" + StringUtil.cleanItemName(new ItemStack(item, 1, meta)), "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta, String identifier) {
		return new ModelResourceLocation(
				"forestry:" + StringUtil.cleanTags(item.getUnlocalizedName(new ItemStack(item, 1, meta))) + identifier,
				"inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, int meta, String modifier, String identifier) {
		return new ModelResourceLocation("forestry:" + modifier + "/" + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item) {
		return new ModelResourceLocation("forestry:" + StringUtil.cleanItemName(item), "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, String identifier) {
		return new ModelResourceLocation("forestry:" + identifier, "inventory");
	}

	@Override
	public ModelResourceLocation getModelLocation(Item item, String modID, String identifier) {
		return new ModelResourceLocation(modID + ":" + identifier, "inventory");
	}

	@Override
	public IModelBaker createNewRenderer() {
		return new ModelBaker();
	}

}

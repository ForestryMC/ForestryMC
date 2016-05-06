/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.models;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.vecmath.Vector3f;

import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelBaker;
import forestry.core.items.ItemCrated;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.storage.PluginStorage;

import gnu.trove.map.hash.TIntObjectHashMap;

@SideOnly(Side.CLIENT)
public class ModelCrate extends BlankItemModel {

	private final Map<String, IBakedModel> crates = Maps.newHashMap();

	private static IModel crateModel;
	private static ModelBlock MODEL_GENERATED;
    private static ModelBlock MODEL_ENTITY;

	
	//Init the model
	public static void initModel(ModelBakeEvent event){
		try {
			crateModel = ModelLoaderRegistry.getModel(new ResourceLocation("forestry:item/crate-filled"));
			MODEL_GENERATED = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, event.getModelLoader(), 17);
			MODEL_ENTITY = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, event.getModelLoader(), 18);
		} catch (Exception e) {
			Log.error("Failed to init the crate model.", e);
		}
	}

	
	//Bake the crate content models
	private List<IBakedModel> bakeContentModels(ItemCrated crateItem, IBakedModel crateModel) {
		List<IBakedModel> models = new ArrayList();

		IBakedModel containedModel = getModel(crateItem.getContained());

		IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> modelResourceLocations = ObfuscationReflectionHelper.getPrivateValue(ItemModelMesherForge.class, (ItemModelMesherForge) Minecraft.getMinecraft().getRenderItem().getItemModelMesher(), 0);

		ModelResourceLocation modelResource = modelResourceLocations.get(crateItem.getContained().getItem()).get(crateItem.getContained().getItemDamage());
		ResourceLocation location = getItemLocation(modelResource);

		models.add(crateModel);
		
		if (hasItemModel(location)) {
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, 0.0625F, 0.5F));
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, -0.0625F, 0.5F));
		} else {
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, 0, 0.5F));
		}
		return models;
	}

	/**
	 * @return The item model {@link ResourceLocation} from a {@link ModelResourceLocation}
	 */
	private ResourceLocation getItemLocation(ModelResourceLocation modelResource) {
		ResourceLocation resourcelocation = new ResourceLocation(modelResource.toString().replaceAll("#.*", ""));
		return new ResourceLocation(resourcelocation.getResourceDomain(), "item/" + resourcelocation.getResourcePath());
	}

	/**
	 * @return Return true, when the model a item model is
	 */
	private boolean hasItemModel(ResourceLocation location) {
		try {
			ModelBlock modelBlock = loadModel(location);
			if (modelBlock == null) {
				return false;
			} else {
				return modelBlock.getRootModel() == MODEL_GENERATED;
			}
		} catch (IOException e) {
			return false;
		}
	}
	
    protected ModelBlock loadModel(ResourceLocation location) throws IOException
    {
        Reader reader = null;
        IResource iresource = null;
        ModelBlock model;

        try
        {
            String s = location.getResourcePath();

            if (!"builtin/generated".equals(s)){
                if ("builtin/entity".equals(s)){
                    model = MODEL_ENTITY;
                    return model;
                }

                if (s.startsWith("builtin/")) {
    				String s1 = s.substring("builtin/".length());
    				if (s1.equals("missing")) {
    					reader = new StringReader("{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
    				} else {
    					throw new FileNotFoundException(location.toString());
    				}
    			}else{
                     iresource = Minecraft.getMinecraft().getResourceManager().getResource(this.getModelLocation(location));
                    reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
                }

                model = ModelBlock.deserialize(reader);
                model.name = location.toString();
                if (model != null && model.getParentLocation() != null) {
    				if (model.getParentLocation().getResourcePath().equals("builtin/generated")) {
    					model.parent = MODEL_GENERATED;
    				} else {
    					try {
    						model.parent = loadModel(model.getParentLocation());
    					} catch (IOException e) {
    						Log.error("Failed to load model.", e);
    					}
    				}
    			} 
                return model;
            }

            model = MODEL_GENERATED;
        }
        finally
        {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly((Closeable)iresource);
        }

        return model;
    }

	private ResourceLocation getModelLocation(ResourceLocation resourceLocation) {
		return new ResourceLocation(resourceLocation.getResourceDomain(), "models/" + resourceLocation.getResourcePath() + ".json");
	}

	/**
	 * @return The baked model from the filled crate
	 */
	private IBakedModel getModel(ItemStack stack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
	}
	
	@Override
	public ItemOverrideList getOverrides() {
		return new CrateOverrideList();
	}
	
	private class CrateOverrideList extends ItemOverrideList{

		public CrateOverrideList() {
			super(Collections.emptyList());
		}
		
		/**
		 * Bake the crated model
		 */
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			ItemCrated crated = (ItemCrated) stack.getItem();
			String crateUID = ItemStackUtil.getItemNameFromRegistry(crated).getResourcePath();
			if (crates.get(crateUID) == null) {
				IBakedModel baseBaked = getModel(new ItemStack(PluginStorage.items.crate, 1, 1));
				//Set the crate color index to 100
				for (BakedQuad quad : baseBaked.getQuads(null, null, 0)) {
					ObfuscationReflectionHelper.setPrivateValue(BakedQuad.class, quad, 100, 1);
				}
				
				crates.put(crateUID, new IPerspectiveAwareModel.MapWrapper(new CrateBakedModel( bakeContentModels(crated, baseBaked)), getTransformations()));
			}
			return crates.get(crateUID);
		}
		
	}
	
	private static class CrateBakedModel extends BlankItemModel{

		public final List<IBakedModel> models;
		
		public CrateBakedModel(List<IBakedModel> models) {
			this.models = models;
		}
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList();
			for(IBakedModel bakedModel : models){
				quads.addAll(bakedModel.getQuads(null, side, rand++));	
			}
			return quads;
		}
		
	}

	private static IModelState getTransformations() {
        TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
        TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
        builder.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
        builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
        builder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
        return new SimpleModelState(builder.build());
	}
	
    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
    {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(tx / 16, ty / 16, tz / 16),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
            new Vector3f(s, s, s),
            null));
    }

    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    private static TRSRTransformation leftify(TRSRTransformation transform)
    {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }
    
	@Override
	public boolean isGui3d() {
		return false;
	}
}

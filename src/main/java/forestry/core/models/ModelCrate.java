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
package forestry.core.models;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.items.ItemCrated;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginStorage;

import gnu.trove.map.hash.TIntObjectHashMap;

@SideOnly(Side.CLIENT)
public class ModelCrate implements ISmartItemModel {
	
	private final Map<String, MultiModel.Baked> crates = Maps.newHashMap();
	
	public static IModel crateModel;
	private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;
	public static ModelBlock MODEL_GENERATED;
	public static ModelBlock MODEL_COMPASS;
	public static ModelBlock MODEL_CLOCK;
	public static ModelLoader loader;
	
	public ModelCrate() {
		textureGetter = new CrateTextureGetter();
	}
	
    /**
     * To bake the contained model
     */
    private ImmutableMap<String, IFlexibleBakedModel> bakeModels(ItemCrated crated){
    	ImmutableMap.Builder<String, IFlexibleBakedModel> pb = ImmutableMap.builder();

        IFlexibleBakedModel flexModel = getModel(crated.getContained());
        
        IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> modelResourceLocations = ObfuscationReflectionHelper.getPrivateValue(ItemModelMesherForge.class, (ItemModelMesherForge)Minecraft.getMinecraft().getRenderItem().getItemModelMesher(), 0);
        
        ModelResourceLocation modelResource = modelResourceLocations.get(crated.getContained().getItem()).get(crated.getContained().getItemDamage());
        ResourceLocation location = getItemLocation(modelResource);
        
        if(hasItemModel(location)){
        	pb.put(String.valueOf(0), new TRSRBakedModel(flexModel, -0.0625F, 0, 0.0625F, 0.5F));
        	pb.put(String.valueOf(1), new TRSRBakedModel(flexModel, -0.0625F, 0, -0.0625F, 0.5F));
        }else{
        	pb.put(String.valueOf(0), new TRSRBakedModel(flexModel, -0.0625F, 0, 0, 0.5F));
        }
        return pb.build();
    }
    
    /**
     * @return The item model {@link ResourceLocation} from the {@link ModelResourceLocation}
     */
    private ResourceLocation getItemLocation(ModelResourceLocation modelResource){
        ResourceLocation resourcelocation = new ResourceLocation(modelResource.toString().replaceAll("#.*", ""));
        return new ResourceLocation(resourcelocation.getResourceDomain(), "item/" + resourcelocation.getResourcePath());
    }
    
    /**
     * @return Return true, when the model from the {@link ResourceLocation} a item model is
     */
    private boolean hasItemModel(ResourceLocation location){
    	try{
	    	ModelBlock p_177581_1_ = loadModel(location);
	        if (p_177581_1_ == null){
	            return false;
	        }else{
	            ModelBlock modelblock = p_177581_1_.getRootModel();
	            return modelblock == MODEL_GENERATED || modelblock == MODEL_COMPASS || modelblock == MODEL_CLOCK;
	        }
    	}catch(Exception e){
    		return false;
    	}
    }
    
    protected ModelBlock loadModel(ResourceLocation p_177594_1_) throws IOException
    {
        String s = p_177594_1_.getResourcePath();

        if ("builtin/generated".equals(s))
        {
            return MODEL_GENERATED;
        }
        else if ("builtin/compass".equals(s))
        {
            return MODEL_COMPASS;
        }
        else if ("builtin/clock".equals(s))
        {
            return MODEL_CLOCK;
        }
        else
        {
            Reader reader;

            if (s.startsWith("builtin/"))
            {
                String s1 = s.substring("builtin/".length());
                if(s1.equals("missing")){
                	reader = new StringReader("{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
                }
                else{
                	throw new FileNotFoundException(p_177594_1_.toString());
                }
            }
            else
            {
                IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(this.getModelLocation(p_177594_1_));
                reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
            }

            ModelBlock model;

            try
            {
                ModelBlock modelblock = ModelBlock.deserialize(reader);
                modelblock.name = p_177594_1_.toString();
                model = modelblock;
            }
            finally
            {
                reader.close();
            }
            
            if(model != null && model.getParentLocation() != null){
                if(model.getParentLocation().getResourcePath().equals("builtin/generated"))
                {
                    model.parent = MODEL_GENERATED;
                }
                else
                {
                    try{
                    	model.parent = loadModel(model.getParentLocation());
                    }catch (IOException e){
                    }
                }
            }

            return model;
        }
    }
    
    protected ResourceLocation getModelLocation(ResourceLocation p_177580_1_)
    {
        return new ResourceLocation(p_177580_1_.getResourceDomain(), "models/" + p_177580_1_.getResourcePath() + ".json");
    }
    
    /**
     * @return The baked model from the filled crate
     */
    private IFlexibleBakedModel getModel(ItemStack stack){
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        if(model == null) {
          return null;
        }
        else if(model instanceof IFlexibleBakedModel) {
         return (IFlexibleBakedModel) model;
        }
        else {
          return new IFlexibleBakedModel.Wrapper(model, DefaultVertexFormats.ITEM);
        }
    }
    
    private IFlexibleBakedModel getModelCrate(){
        IFlexibleBakedModel flexModel;
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(new ItemStack(PluginStorage.items.crate, 1, 1));
        if(model == null) {
          return null;
        }
        else if(model instanceof IFlexibleBakedModel) {
        	flexModel = (IFlexibleBakedModel) model;
        }
        else {
        	 flexModel =  new IFlexibleBakedModel.Wrapper(model, DefaultVertexFormats.ITEM);
        }
        return crateModel.bake(crateModel.getDefaultState(), flexModel.getFormat(), textureGetter);
    }

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing facing) {
		return null;
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return null;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return null;
	}

	/**
	 * Bake the crated model
	 */
	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		ItemCrated crated = (ItemCrated) stack.getItem();
		String crateUID = StringUtil.cleanItemName(crated);
		if(crates.get(crateUID) == null){
		    IFlexibleBakedModel baseBaked = getModelCrate();
		        
		    //Set the crate color index to 100
		    for(BakedQuad quad : baseBaked.getGeneralQuads()){
		      	ObfuscationReflectionHelper.setPrivateValue(BakedQuad.class, quad, 100, 1);
		    }
		    crates.put(crateUID, new MultiModel.Baked(baseBaked, bakeModels(crated)));
		}
		return crates.get(crateUID);
	}

	private static class CrateTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	}
}
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

import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.MultiLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import forestry.core.utils.Log;
public class ModelCrate implements IModelCustomData<ModelCrate> {
    public static final ModelCrate instance = new ModelCrate("cratedStone", "forestry", new ModelResourceLocation("forestry:crates", "cratedStone"), false);

    private final String containedUID;
    private final String modID;
    private final ResourceLocation containedLocation;
    private final boolean isContainedItem;

    /**
     * @param baseLocation The location of the crate model
     * @param UID The UID of the crate
     * @param modID The modId of the creat item
     * @param containedLocation The location of the model from the contained item
     */
    private ModelCrate(String UID, String modID, ResourceLocation containedLocation, boolean isContainedItem){
        this.containedUID = UID;
        this.modID = modID;
        this.containedLocation = containedLocation;
        this.isContainedItem = isContainedItem;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.<ResourceLocation>of(new ModelResourceLocation("forestry:crates", "crate"), containedLocation);
    }

    @Override
    public Collection<ResourceLocation> getTextures(){
        return ImmutableList.of();
    }
    
    /**
     * 
     * @param loc The location of the model
     * @return A model from the {@link ModelLoaderRegistry} 
     */
    private static IModel getModel(ResourceLocation loc){
        IModel model;
        try{
            model = ModelLoaderRegistry.getModel(loc);
        }
        catch (IOException e){
            Log.logThrowable("Couldn't load Crate Model dependency: %s", e, loc);
            model = ModelLoaderRegistry.getMissingModel();
        }
     	return model;
    }

    /**
     * To bake the base and the contained model
     */
    private static ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> buildModels(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ModelCrate modelToBake){
        ImmutableMap.Builder<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> builder = ImmutableMap.builder();
        
        IModel base = getModel(new ModelResourceLocation("forestry:crates", "crate"));
        IFlexibleBakedModel baseBaked = base.bake(new ModelStateComposition(state, base.getDefaultState()), format, bakedTextureGetter);
        
        //Set the crate color index to 100
        for(BakedQuad quad : baseBaked.getGeneralQuads()){
      		ObfuscationReflectionHelper.setPrivateValue(BakedQuad.class, quad, 100, 1);
        }
        builder.put(Optional.<EnumWorldBlockLayer>absent(), baseBaked);
        builder.put(Optional.of(EnumWorldBlockLayer.SOLID), baseBaked);


        
        IModel content = getModel(modelToBake.containedLocation);
        if(modelToBake.isContainedItem){
        	builder.put(Optional.of(EnumWorldBlockLayer.CUTOUT), new TRSRBakedModel(content.bake(state, format, bakedTextureGetter), -0.0625F, 0, 0.0625F, 0.5F));
        	builder.put(Optional.of(EnumWorldBlockLayer.CUTOUT_MIPPED), new TRSRBakedModel(content.bake(state, format, bakedTextureGetter), -0.0625F, 0, -0.0625F, 0.5F));
        }else{
        	builder.put(Optional.of(EnumWorldBlockLayer.CUTOUT), new TRSRBakedModel(content.bake(state, format, bakedTextureGetter), -0.0625F, 0, 0, 0.5F));
        }
        return builder.build();
    }

    /**
     * Bake the crate model and the content of the crate
     */
    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
        IModel missing = ModelLoaderRegistry.getMissingModel();
        return new MultiLayerModel.MultiLayerBakedModel(
            buildModels(state, format, bakedTextureGetter, this),
            missing.bake(missing.getDefaultState(), format, bakedTextureGetter),
            format,
            IPerspectiveAwareModel.MapWrapper.getTransforms(state)
        );
    }

    @Override
    public IModelState getDefaultState(){
        return TRSRTransformation.identity();
    }

    /**
     * Create a crate model from the customData
     */
    @Override
    public IModel process(ImmutableMap<String, String> customData){
        ResourceLocation location = null;
        String UID = "";
        boolean isContainedItem = false;
        String modID = "";
        for(String key : customData.keySet()){
        	if("uid".equals(key)){
            	UID = getString(customData.get(key));
            }
            else if("modID".equals(key)){
            	modID = getString(customData.get(key));
            }
            else if("location".equals(key)){
            	location = getLocation(customData.get(key));
            }
            else if("isItem".equals(key)){
            	isContainedItem = getBoolean(customData.get(key));
            }
        }
        if(modID == null || modID.equals("")){
        	modID = "forestry";
        }
        if(location == null || UID == null || UID.equals("")) {
        	return instance;
        }
        return new ModelCrate(UID, modID, location, isContainedItem);
    }

    private ResourceLocation getLocation(String json){
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()){
        	if(e.getAsString().contains("#")){
                return new ModelResourceLocation(e.getAsString());
        	}else{
                return new ResourceLocation(e.getAsString());
        	}
        }
        return new ModelResourceLocation("builtin/missing", "missing");
    }
    
    private String getString(String json){
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()){
            return e.getAsString();
        }
        return null;
    }
    
    private boolean getBoolean(String json){
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean()){
            return e.getAsBoolean();
        }
        return false;
    }

    /**
     * The model loader to load crate models from the blockstate file
     */
    public static enum Loader implements ICustomModelLoader{
        instance;

        @Override
		public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
		public boolean accepts(ResourceLocation modelLocation){
            return modelLocation.getResourceDomain().equals("forestry") && 
                modelLocation.getResourcePath().equals("models/block/crate");
        }

        @Override
		public IModel loadModel(ResourceLocation modelLocation){
            return ModelCrate.instance;
        }
    }
}
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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import forestry.core.utils.Log;
public class ModelCrate implements IModelCustomData<ModelCrate> {
    public static final ModelCrate instance = new ModelCrate(new ModelResourceLocation("forestry:crates", "crate"), EnumWorldBlockLayer.CUTOUT, "cratedStone", "forestry", new ModelResourceLocation("forestry:crates", "cratedStone"));

    private final ModelResourceLocation baseLocation;
    private final String containedUID;
    private final String modID;
    private final ResourceLocation containedLocation;
    private final EnumWorldBlockLayer containedLayer;

    /**
     * @param baseLocation The location of the crate model
     * @param containedLayer The layer for the rendering from the contained item model
     * @param UID The UID of the crate
     * @param modID The modId of the creat item
     * @param location The location of the model from the contained item
     */
    private ModelCrate(ModelResourceLocation baseLocation, EnumWorldBlockLayer containedLayer, String UID, String modID, ResourceLocation location){
        this.baseLocation = baseLocation;
        this.containedLayer = containedLayer;
        this.containedUID = UID;
        this.modID = modID;
        this.containedLocation = location;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.<ResourceLocation>of(baseLocation, containedLocation);
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
    private static ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> buildModels(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, IModel crateModel, ModelCrate modelToBake){
        ImmutableMap.Builder<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> builder = ImmutableMap.builder();
        
        IModel base = getModel(modelToBake.baseLocation);
        IFlexibleBakedModel baseBaked = base.bake(new ModelStateComposition(state, base.getDefaultState()), format, bakedTextureGetter);
        builder.put(Optional.<EnumWorldBlockLayer>absent(), baseBaked);
        builder.put(Optional.of(EnumWorldBlockLayer.SOLID), baseBaked);
        		
        IModel content = getModel(modelToBake.containedLocation);
        builder.put(Optional.of(modelToBake.containedLayer), content.bake(new ModelStateComposition(state, crateModel.getDefaultState()), format, bakedTextureGetter));
        return builder.build();
    }

    /**
     * Bake the crate model and the content of the crate
     */
    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
        IModel missing = ModelLoaderRegistry.getMissingModel();
        IModel creat;
        try {
			creat = ModelLoaderRegistry.getModel(new ModelResourceLocation(modID + ":crates", containedUID));
		} catch (IOException e) {
			e.printStackTrace();
			return missing.bake(state, format, bakedTextureGetter);
		}
        if(creat == null){
        	return missing.bake(state, format, bakedTextureGetter);
        }
        return new MultiLayerModel.MultiLayerBakedModel(
            buildModels(state, format, bakedTextureGetter, creat, this),
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
        ImmutableMap.Builder<Optional<EnumWorldBlockLayer>, ModelResourceLocation> builder = ImmutableMap.builder();
        ModelResourceLocation baseLocation = null;
        ResourceLocation location = null;
        String UID = "";
        String modID = "";
        EnumWorldBlockLayer layer = null;
        for(String key : customData.keySet()){
            if("base".equals(key)){
                baseLocation = (ModelResourceLocation) getLocation(customData.get(key));
            }
            else if("uid".equals(key)){
            	UID = getString(customData.get(key));
            }
            else if("modID".equals(key)){
            	modID = getString(customData.get(key));
            }
            else if("location".equals(key)){
            	location = getLocation(customData.get(key));
            }
            else if("layer".equals(key)){
        		String layerName = getString(customData.get(key));
            	for(EnumWorldBlockLayer l : EnumWorldBlockLayer.values()){
            		if(l.toString().equals(layerName)){
            			layer = l;
            			break;
            		}
            	}
            }
        }
        if(modID == null){
        	modID = "forestry";
        }
        if(baseLocation == null || layer == null || location == null || UID == null) {
        	return instance;
        }
        return new ModelCrate(baseLocation, layer, UID, modID, location);
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
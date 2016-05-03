// TODO: fix crate rendering

///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.core.models;
//
//import com.google.common.base.Charsets;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//
//import javax.vecmath.Vector3f;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.StringReader;
//import java.util.IdentityHashMap;
//import java.util.Map;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.block.model.BakedQuad;
//import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
//import net.minecraft.client.renderer.block.model.ModelBlock;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.client.resources.IResource;
//import net.minecraft.client.resources.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ModelBakery;
//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//
//import net.minecraftforge.client.ItemModelMesherForge;
//import net.minecraftforge.client.event.ModelBakeEvent;
//import net.minecraftforge.client.model.IFlexibleBakedModel;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.client.model.IModelState;
//import net.minecraftforge.client.model.IPerspectiveAwareModel;
//import net.minecraftforge.client.model.ModelLoaderRegistry;
//import net.minecraftforge.client.model.MultiModel;
//import net.minecraftforge.client.model.SimpleModelState;
//import net.minecraftforge.client.model.TRSRTransformation;
//import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import forestry.core.items.ItemCrated;
//import forestry.core.utils.ItemStackUtil;
//import forestry.core.utils.Log;
//import forestry.storage.PluginStorage;
//
//import gnu.trove.map.hash.TIntObjectHashMap;
//
//@SideOnly(Side.CLIENT)
//public class ModelCrate extends BlankItemModel {
//
//	private final Map<String, IModel> crates = Maps.newHashMap();
//
//	private static IModel crateModel;
//	private static ModelBlock MODEL_GENERATED;
//	private static ModelBlock MODEL_COMPASS;
//	private static ModelBlock MODEL_CLOCK;
//
//	/**
//	 * Init the model
//	 */
//	public static void initModel(ModelBakeEvent event){
//		try {
//			crateModel = ModelLoaderRegistry.getModel(new ModelResourceLocation("forestry:crate-filled", "inventory"));
//			MODEL_GENERATED = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, event.modelLoader, 14);
//			MODEL_COMPASS = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, event.modelLoader, 15);
//			MODEL_CLOCK = ObfuscationReflectionHelper.getPrivateValue(ModelBakery.class, event.modelLoader, 16);
//		} catch (Exception e) {
//			Log.error("Failed to init the crate model.", e);
//		}
//	}
//
//	/**
//	 * Bake the crate content models
//	 */
//	private ImmutableMap<String, IFlexibleBakedModel> bakeContentModels(ItemCrated crateItem) {
//		ImmutableMap.Builder<String, IFlexibleBakedModel> pb = ImmutableMap.builder();
//
//		IFlexibleBakedModel containedModel = getModel(crateItem.getContained());
//
//		IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> modelResourceLocations = ObfuscationReflectionHelper.getPrivateValue(ItemModelMesherForge.class, (ItemModelMesherForge) Minecraft.getMinecraft().getRenderItem().getItemModelMesher(), 0);
//
//		ModelResourceLocation modelResource = modelResourceLocations.get(crateItem.getContained().getItem()).get(crateItem.getContained().getItemDamage());
//		ResourceLocation location = getItemLocation(modelResource);
//
//		if (hasItemModel(location)) {
//			pb.put(String.valueOf(0), new TRSRBakedModel(containedModel, -0.0625F, 0, 0.0625F, 0.5F));
//			pb.put(String.valueOf(1), new TRSRBakedModel(containedModel, -0.0625F, 0, -0.0625F, 0.5F));
//		} else {
//			pb.put(String.valueOf(0), new TRSRBakedModel(containedModel, -0.0625F, 0, 0, 0.5F));
//		}
//		return pb.build();
//	}
//
//	/**
//	 * @return The item model {@link ResourceLocation} from a {@link ModelResourceLocation}
//	 */
//	private ResourceLocation getItemLocation(ModelResourceLocation modelResource) {
//		ResourceLocation resourcelocation = new ResourceLocation(modelResource.toString().replaceAll("#.*", ""));
//		return new ResourceLocation(resourcelocation.getResourceDomain(), "item/" + resourcelocation.getResourcePath());
//	}
//
//	/**
//	 * @return Return true, when the model a item model is
//	 */
//	private boolean hasItemModel(ResourceLocation location) {
//		try {
//			ModelBlock modelBlock = loadModel(location);
//			if (modelBlock == null) {
//				return false;
//			} else {
//				ModelBlock rootModelBlock = modelBlock.getRootModel();
//				return rootModelBlock == MODEL_GENERATED || rootModelBlock == MODEL_COMPASS || rootModelBlock == MODEL_CLOCK;
//			}
//		} catch (IOException e) {
//			return false;
//		}
//	}
//
//	private ModelBlock loadModel(ResourceLocation location) throws IOException {
//		String s = location.getResourcePath();
//
//		if ("builtin/generated".equals(s)) {
//			return MODEL_GENERATED;
//		} else if ("builtin/compass".equals(s)) {
//			return MODEL_COMPASS;
//		} else if ("builtin/clock".equals(s)) {
//			return MODEL_CLOCK;
//		} else {
//			Reader reader;
//
//			if (s.startsWith("builtin/")) {
//				String s1 = s.substring("builtin/".length());
//				if (s1.equals("missing")) {
//					reader = new StringReader("{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
//				} else {
//					throw new FileNotFoundException(location.toString());
//				}
//			} else {
//				IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(this.getModelLocation(location));
//				reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
//			}
//
//			ModelBlock model;
//
//			try {
//				ModelBlock modelblock = ModelBlock.deserialize(reader);
//				modelblock.name = location.toString();
//				model = modelblock;
//			} finally {
//				try {
//					reader.close();
//				} catch (IOException e) {
//					Log.error("Failed to close crate model reader.", e);
//				}
//			}
//
//			if (model != null && model.getParentLocation() != null) {
//				if (model.getParentLocation().getResourcePath().equals("builtin/generated")) {
//					model.parent = MODEL_GENERATED;
//				} else {
//					try {
//						model.parent = loadModel(model.getParentLocation());
//					} catch (IOException e) {
//						Log.error("Failed to load model.", e);
//					}
//				}
//			}
//
//			return model;
//		}
//	}
//
//	private ResourceLocation getModelLocation(ResourceLocation resourceLocation) {
//		return new ResourceLocation(resourceLocation.getResourceDomain(), "models/" + resourceLocation.getResourcePath() + ".json");
//	}
//
//	/**
//	 * @return The baked model from the filled crate
//	 */
//	private IFlexibleBakedModel getModel(ItemStack stack) {
//		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
//		if (model == null) {
//			return null;
//		} else if (model instanceof IFlexibleBakedModel) {
//			return (IFlexibleBakedModel) model;
//		} else {
//			return new IFlexibleBakedModel.Wrapper(model, DefaultVertexFormats.ITEM);
//		}
//	}
//
//	/**
//	 * Bake the crated model
//	 */
//	@Override
//	public IBakedModel handleItemState(ItemStack stack) {
//		ItemCrated crated = (ItemCrated) stack.getItem();
//		String crateUID = ItemStackUtil.getItemNameFromRegistry(crated).getResourcePath();
//		if (crates.get(crateUID) == null) {
//			IFlexibleBakedModel baseBaked = getModel(new ItemStack(PluginStorage.items.crate, 1, 1));
//			//Set the crate color index to 100
//			for (BakedQuad quad : baseBaked.getGeneralQuads()) {
//				ObfuscationReflectionHelper.setPrivateValue(BakedQuad.class, quad, 100, 1);
//			}
//			crates.put(crateUID, new MultiModel.Baked(null, true, new IPerspectiveAwareModel.MapWrapper(baseBaked, getTransformations()), bakeContentModels(crated)));
//		}
//		return crates.get(crateUID);
//	}
//
//	public static IModelState getTransformations() {
//		TRSRTransformation thirdperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
//                new Vector3f(0, 1f / 16, -3f / 16),
//                TRSRTransformation.quatFromYXZDegrees(new Vector3f(-90, 0, 0)),
//                new Vector3f(0.55f, 0.55f, 0.55f),
//                null));
//            TRSRTransformation firstperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
//                new Vector3f(0, 4f / 16, 2f / 16),
//                TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)),
//                new Vector3f(1.7f, 1.7f, 1.7f),
//                null));
//            return new SimpleModelState(ImmutableMap.of(TransformType.THIRD_PERSON, thirdperson, TransformType.FIRST_PERSON, firstperson));
//	}
//
//	@Override
//	public boolean isGui3d() {
//		return false;
//	}
//}

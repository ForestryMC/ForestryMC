package forestry.arboriculture.render;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IColoredBakedQuad;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.ModelFluid.BakedFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.lwjgl.BufferUtils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import forestry.api.core.IModelRenderer;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.render.ModelManager;

public class ModelFruitPod implements ISmartBlockModel {

	public IRetexturableModel modelCocoa;
	public ResourceLocation location;
	public ModelRotation modelRotation;

	public ModelFruitPod(ResourceLocation location, ModelRotation modelRotation) {
		this.location = location;
		this.modelRotation = modelRotation;
	}

	@Override
	public List getFaceQuads(EnumFacing p_177551_1_) {
		return Collections.emptyList();
	}

	@Override
	public List getGeneralQuads() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		IExtendedBlockState extend = (IExtendedBlockState) state;
		IModelRenderer renderer = ModelManager.getInstance().createNewRenderer();
		Block blk = state.getBlock();
		IBlockAccess world = extend.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = extend.getValue(UnlistedBlockPos.POS);
		renderer.setRenderBoundsFromBlock(blk);
		if (modelCocoa == null) {
			try {
				modelCocoa = (IRetexturableModel) ModelLoaderRegistry.getModel(location);
			} catch (IOException e) {
				Proxies.log.warning("Failed to find Model for (" + location.toString() + ") in the Forge registry.");
			}
		}
		return bakeModel(world, pos);
	}

	public IBakedModel bakeModel(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		TileFruitPod pod = (TileFruitPod) tile;
		pod.getIcon();
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		};
		textureGetter.apply(new ResourceLocation(pod.getIcon().toString()));
		return modelCocoa.bake(modelRotation, Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
	}
}
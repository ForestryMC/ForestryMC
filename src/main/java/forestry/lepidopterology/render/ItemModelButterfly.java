package forestry.lepidopterology.render;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import forestry.api.core.IModelRenderer;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.render.ModelManager;
import forestry.plugins.PluginLepidopterology;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;

public class ItemModelButterfly implements ISmartItemModel {

	public IRetexturableModel modelButterfly;
	public ResourceLocation location;

	public ItemModelButterfly(ResourceLocation location) {
		this.location = location;
	}

	@Override
	public List getFaceQuads(EnumFacing p_177551_1_) {
		return null;
	}

	@Override
	public List getGeneralQuads() {
		return null;
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
	public IBakedModel handleItemState(ItemStack item) {
		try {
			modelButterfly = (IRetexturableModel) ModelLoaderRegistry.getModel(location);
		} catch (IOException e) {
			Proxies.log.warning(
					"Failed to find Butterfly Model for (" + location.toString() + ") in the Forestry registry.");
		}
		IButterfly butterfly = PluginLepidopterology.butterflyInterface.getMember(item);
		if (modelButterfly == null || butterfly == null)
			return null;
		return bakeModel(butterfly);
	}

	public IBakedModel bakeModel(IButterfly butterfly) {
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		};
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		textures.put("Butterfly2texture", butterfly.getGenome().getSecondary().getEntityTexture().toString());
		modelButterfly = (IRetexturableModel) modelButterfly.retexture(textures.build());
		return modelButterfly.bake(ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
	}

	public static float getIrregularWingYaw(long flapping, float flap) {
		long irregular = flapping / 1024;
		float wingYaw;

		if (irregular % 11 == 0) {
			wingYaw = 0.75f;
		} else {
			if (irregular % 7 == 0) {
				flap *= 4;
				flap = flap % 1;
			} else if (irregular % 19 == 0) {
				flap *= 6;
				flap = flap % 1;
			}
			wingYaw = getRegularWingYaw(flap);
		}

		return wingYaw;
	}

	private static float getRegularWingYaw(float flap) {
		return flap < 0.5 ? 0.75f + flap : 1.75f - flap;
	}

}

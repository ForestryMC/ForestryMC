package forestry.apiculture.render;

import java.util.List;

import forestry.apiculture.items.ItemHabitatLocator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

public class ModelHabitatLocator implements ISmartItemModel {

	public IBakedModel model;

	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(
			"forestry:habitatlocator", "inventory");

	public ModelHabitatLocator(IBakedModel model) {
		this.model = model;
	}

	@Override
	public List getFaceQuads(EnumFacing side) {
		return model.getFaceQuads(side);
	}

	@Override
	public List getGeneralQuads() {
		return model.getGeneralQuads();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return model.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return model.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return model.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return ItemHabitatLocator.icon;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return model.getItemCameraTransforms();
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		return model;
	}

}

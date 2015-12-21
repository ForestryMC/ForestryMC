package forestry.farming.render;

import forestry.api.core.IModelRenderer;
import forestry.core.render.OverlayRenderingHandler;
import forestry.core.render.TextureManager;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumBlockFarmType;
import forestry.farming.tiles.TileFarm;
import forestry.plugins.PluginFarming;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FarmBlockRenderer extends OverlayRenderingHandler {

	@Override
	public void renderInventory(Block block, ItemStack item, IModelRenderer renderer) {
		if (block == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		TextureAtlasSprite[] textures = getSprites(type);
		TextureAtlasSprite[] texturesOverlay = getOverlaySprites(EnumBlockFarmType.VALUES[item.getItemDamage()]);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		// GL11.glTranslatef(translateX, translateY, translateZ);
		renderer.renderFaceYNeg(null, textures[0]);
		renderer.renderFaceYNeg(null, texturesOverlay[0]);

		renderer.renderFaceYPos(null, textures[1]);
		renderer.renderFaceYPos(null, texturesOverlay[1]);

		renderer.renderFaceZNeg(null, textures[2]);
		renderer.renderFaceZNeg(null, texturesOverlay[2]);

		renderer.renderFaceZPos(null, textures[3]);
		renderer.renderFaceZPos(null, texturesOverlay[3]);

		renderer.renderFaceXNeg(null, textures[4]);
		renderer.renderFaceXNeg(null, texturesOverlay[4]);

		renderer.renderFaceXPos(null, textures[5]);
		renderer.renderFaceXPos(null, texturesOverlay[5]);

		// GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelRenderer renderer) {

		TileFarm farm = (TileFarm) world.getTileEntity(pos);

		TextureAtlasSprite[] textures = getSprites(farm.getFarmBlockTexture());
		TextureAtlasSprite[] texturesOverlay = getOverlaySprites(EnumBlockFarmType.VALUES[farm.getBlockMetadata()]);

		// Render the plain block.
		renderer.renderStandardBlock(block, pos, textures);
		renderFarmOverlay(world, PluginFarming.blocks.farm, pos, renderer, texturesOverlay);

		return true;
	}

	private static void renderFarmOverlay(IBlockAccess world, BlockFarm block, BlockPos pos, IModelRenderer renderer,
			TextureAtlasSprite[] texturesOverlay) {
		int mixedBrightness = block.getMixedBrightnessForBlock(world, pos);

		float adjR = 0.5f;
		float adjG = 0.5f;
		float adjB = 0.5f;

		renderBottomFace(world, block, pos, renderer, texturesOverlay[0], mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, pos, renderer, texturesOverlay[1], mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, pos, renderer, texturesOverlay[2], mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, pos, renderer, texturesOverlay[3], mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, pos, renderer, texturesOverlay[4], mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, pos, renderer, texturesOverlay[5], mixedBrightness, adjR, adjG, adjB);
	}

	private static TextureAtlasSprite[] getSprites(EnumFarmBlockTexture texture) {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		textures[0] = EnumFarmBlockTexture.getSprite(texture, 0);
		textures[1] = EnumFarmBlockTexture.getSprite(texture, 1);
		textures[2] = EnumFarmBlockTexture.getSprite(texture, 2);
		textures[3] = EnumFarmBlockTexture.getSprite(texture, 3);
		textures[4] = EnumFarmBlockTexture.getSprite(texture, 4);
		textures[5] = EnumFarmBlockTexture.getSprite(texture, 5);
		return textures;
	}

	private static TextureAtlasSprite[] getOverlaySprites(EnumBlockFarmType type) {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		textures[0] = getOverlaySprite(type, 0);
		textures[1] = getOverlaySprite(type, 1);
		textures[2] = getOverlaySprite(type, 2);
		textures[3] = getOverlaySprite(type, 3);
		textures[4] = getOverlaySprite(type, 4);
		textures[5] = getOverlaySprite(type, 5);
		return textures;
	}

	private static TextureAtlasSprite getOverlaySprite(EnumBlockFarmType texture, int side) {
		return EnumFarmBlockTexture.getSprite(texture, side);
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return getSprites(EnumFarmBlockTexture.BRICK)[0];
	}

}

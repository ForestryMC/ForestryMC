package forestry.farming.models;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockOverlay;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.tiles.TileFarm;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ModelFarmBlock extends ModelBlockOverlay<BlockFarm> {

	@Override
	public void renderInventory(BlockFarm blockFarm, ItemStack item, IModelBaker baker) {
		if (blockFarm == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		TextureAtlasSprite[] sprites = getSprites(type);
		TextureAtlasSprite[] overlaySprites = getOverlaySprites(EnumFarmBlockType.VALUES[item.getItemDamage()]);

		baker.renderFaceYNeg(null, sprites[0]);
		baker.renderFaceYNeg(null, overlaySprites[0]);

		baker.renderFaceYPos(null, sprites[1]);
		baker.renderFaceYPos(null, overlaySprites[1]);

		baker.renderFaceZNeg(null, sprites[2]);
		baker.renderFaceZNeg(null, overlaySprites[2]);

		baker.renderFaceZPos(null, sprites[3]);
		baker.renderFaceZPos(null, overlaySprites[3]);

		baker.renderFaceXNeg(null, sprites[4]);
		baker.renderFaceXNeg(null, overlaySprites[4]);

		baker.renderFaceXPos(null, sprites[5]);
		baker.renderFaceXPos(null, overlaySprites[5]);

		blockFarm.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderInWorld(BlockFarm blockFarm, IBlockAccess world, BlockPos pos, IModelBaker baker) {

		TileFarm farm = (TileFarm) world.getTileEntity(pos);

		TextureAtlasSprite[] textures = getSprites(farm.getFarmBlockTexture());
		TextureAtlasSprite[] texturesOverlay = getOverlaySprites(farm.getFarmBlockType());

		// Render the plain block.
		baker.renderStandardBlock(blockFarm, pos, textures, 0);
		renderFarmOverlay(world, blockFarm, pos, baker, texturesOverlay);

		return true;
	}

	private void renderFarmOverlay(IBlockAccess world, BlockFarm block, BlockPos pos, IModelBaker renderer, TextureAtlasSprite[] overlaySprites) {
		renderBottomFace(world, block, pos, renderer, overlaySprites[0], 0);
		renderTopFace(world, block, pos, renderer, overlaySprites[1], 0);
		renderEastFace(world, block, pos, renderer, overlaySprites[2], 0);
		renderWestFace(world, block, pos, renderer, overlaySprites[3], 0);
		renderNorthFace(world, block, pos, renderer, overlaySprites[4], 0);
		renderSouthFace(world, block, pos, renderer, overlaySprites[5], 0);
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

	private static TextureAtlasSprite[] getOverlaySprites(EnumFarmBlockType farmType) {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		textures[0] = getOverlaySprite(farmType, 0);
		textures[1] = getOverlaySprite(farmType, 1);
		textures[2] = getOverlaySprite(farmType, 2);
		textures[3] = getOverlaySprite(farmType, 3);
		textures[4] = getOverlaySprite(farmType, 4);
		textures[5] = getOverlaySprite(farmType, 5);
		return textures;
	}

	private static TextureAtlasSprite getOverlaySprite(EnumFarmBlockType texture, int side) {
		return EnumFarmBlockType.getSprite(texture, side);
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getSprites(EnumFarmBlockTexture.BRICK)[0];
	}

}

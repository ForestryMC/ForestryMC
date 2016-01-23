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
	public void renderInventory(BlockFarm blockFarm, ItemStack item, IModelBaker backer) {
		if (blockFarm == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		TextureAtlasSprite[] sprites = getSprites(type);
		TextureAtlasSprite[] overlaySprites = getOverlaySprites(EnumFarmBlockType.VALUES[item.getItemDamage()]);

		backer.renderFaceYNeg(null, sprites[0]);
		backer.renderFaceYNeg(null, overlaySprites[0]);

		backer.renderFaceYPos(null, sprites[1]);
		backer.renderFaceYPos(null, overlaySprites[1]);

		backer.renderFaceZNeg(null, sprites[2]);
		backer.renderFaceZNeg(null, overlaySprites[2]);

		backer.renderFaceZPos(null, sprites[3]);
		backer.renderFaceZPos(null, overlaySprites[3]);

		backer.renderFaceXNeg(null, sprites[4]);
		backer.renderFaceXNeg(null, overlaySprites[4]);

		backer.renderFaceXPos(null, sprites[5]);
		backer.renderFaceXPos(null, overlaySprites[5]);

		blockFarm.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderInWorld(BlockFarm blockFarm, IBlockAccess world, BlockPos pos, IModelBaker backer) {

		TileFarm farm = (TileFarm) world.getTileEntity(pos);

		TextureAtlasSprite[] textures = getSprites(farm.getFarmBlockTexture());
		TextureAtlasSprite[] texturesOverlay = getOverlaySprites(farm.getFarmBlockType());

		// Render the plain block.
		backer.renderStandardBlock(blockFarm, pos, textures);
		renderFarmOverlay(world, blockFarm, pos, backer, texturesOverlay);

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

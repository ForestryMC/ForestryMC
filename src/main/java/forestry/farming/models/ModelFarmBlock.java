package forestry.farming.models;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockOverlay;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.tiles.TileFarm;

public class ModelFarmBlock extends ModelBlockOverlay<BlockFarm> {

	public ModelFarmBlock() {
		super(BlockFarm.class);
	}

	@Override
	public void renderInventory(BlockFarm blockFarm, ItemStack item, IModelBaker baker) {
		if (blockFarm == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		baker.addBlockModel(blockFarm, null, getSprites(type), 0);
		baker.addBlockModel(blockFarm, null, getOverlaySprites(EnumFarmBlockType.VALUES[item.getItemDamage()]), 0);
	}

	@Override
	public boolean renderInWorld(BlockFarm blockFarm, IBlockAccess world, BlockPos pos, IModelBaker baker) {

		TileFarm farm = (TileFarm) world.getTileEntity(pos);

		TextureAtlasSprite[] textures = getSprites(farm.getFarmBlockTexture());
		TextureAtlasSprite[] texturesOverlay = getOverlaySprites(farm.getFarmBlockType());

		// Render the plain block.
		baker.addBlockModel(blockFarm, pos, getSprites(farm.getFarmBlockTexture()), 0);
		baker.addBlockModel(blockFarm, pos, getOverlaySprites(farm.getFarmBlockType()), 0);

		return true;
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

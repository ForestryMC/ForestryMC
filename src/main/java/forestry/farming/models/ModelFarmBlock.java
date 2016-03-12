package forestry.farming.models;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
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
	public void bakeInventoryBlock(BlockFarm blockFarm, ItemStack item, IModelBaker baker) {
		if (blockFarm == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		// Add the plain block.
		baker.addBlockModel(blockFarm, null, getSprites(type), 0);
		// Add the overlay block.
		baker.addBlockModel(blockFarm, null, getOverlaySprites(EnumFarmBlockType.VALUES[item.getItemDamage()]), 0);
	}

	@Override
	public void bakeWorldBlock(BlockFarm blockFarm, IBlockAccess world, BlockPos pos, IExtendedBlockState stateExtended, IModelBaker baker) {

		TileFarm farm = (TileFarm) world.getTileEntity(pos);

		TextureAtlasSprite[] textures = getSprites(farm.getFarmBlockTexture());

		// Add the plain block.
		baker.addBlockModel(blockFarm, pos, textures, 0);
		// Add the overlay block.
		baker.addBlockModel(blockFarm, pos, getOverlaySprites(farm.getFarmBlockType()), 0);
		
		// Set the particle sprite
		baker.setParticleSprite(textures[0]);
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
		textures[0] = EnumFarmBlockType.getSprite(farmType, 0);
		textures[1] = EnumFarmBlockType.getSprite(farmType, 1);
		textures[2] = EnumFarmBlockType.getSprite(farmType, 2);
		textures[3] = EnumFarmBlockType.getSprite(farmType, 3);
		textures[4] = EnumFarmBlockType.getSprite(farmType, 4);
		textures[5] = EnumFarmBlockType.getSprite(farmType, 5);
		return textures;
	}

}

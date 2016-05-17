package forestry.farming.models;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockDefault;
import forestry.core.tiles.TileUtil;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.tiles.TileFarm;

public class ModelFarmBlock extends ModelBlockDefault<BlockFarm> {

	public ModelFarmBlock() {
		super(BlockFarm.class);
	}

	@Override
	public void bakeInventoryBlock(@Nonnull BlockFarm blockFarm, @Nonnull ItemStack item, @Nonnull IModelBaker baker) {
		if (blockFarm == null) {
			return;
		}

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		// Add the plain block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, null, getSprites(type), 0);
		// Add the overlay block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, null, getOverlaySprites(EnumFarmBlockType.VALUES[item.getItemDamage()]), 0);
	}

	@Override
	public void bakeWorldBlock(@Nonnull BlockFarm blockFarm, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IExtendedBlockState stateExtended, @Nonnull IModelBaker baker) {

		TileFarm farm = TileUtil.getTile(world, pos, TileFarm.class);

		EnumFarmBlockTexture texture = EnumFarmBlockTexture.BRICK;
		TextureAtlasSprite[] overlayTextures = getOverlaySprites(EnumFarmBlockType.PLAIN);
		
		if(farm != null){
			texture = farm.getFarmBlockTexture();
			overlayTextures = getOverlaySprites(farm.getFarmBlockType());
		}
		
		TextureAtlasSprite[] textures = getSprites(texture);

		// Add the plain block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, pos, textures, 0);
		// Add the overlay block.
		baker.addBlockModel(blockFarm, Block.FULL_BLOCK_AABB, pos, overlayTextures, 0);
		
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

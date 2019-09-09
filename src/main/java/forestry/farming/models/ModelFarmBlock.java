package forestry.farming.models;

import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;

//TODO - I think the farm block nneds flattening
@OnlyIn(Dist.CLIENT)
public class ModelFarmBlock extends ModelBlockCached<BlockFarm, ModelFarmBlock.Key> {

	public static class Key {
		public final EnumFarmBlockTexture texture;
		public final EnumFarmBlockType type;
		private final int hashCode;

		public Key(EnumFarmBlockTexture texture, EnumFarmBlockType type) {
			this.texture = texture;
			this.type = type;
			this.hashCode = Objects.hash(texture, type);
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Key)) {
				return false;
			} else {
				Key otherKey = (Key) other;
				return otherKey.texture == texture && otherKey.type == type;
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}
	}

	public ModelFarmBlock() {
		super(BlockFarm.class);
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(stack.getTag());
		EnumFarmBlockType type = EnumFarmBlockType.VALUES[0];//stack.getItemDamage()];

		return new Key(texture, type);
	}

	@Override
	protected Key getWorldKey(BlockState state, IModelData extraData) {
		//		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		//		IBlockReader world = stateExtended.getComb(UnlistedBlockAccess.BLOCKACCESS);
		//		BlockPos pos = stateExtended.getComb(UnlistedBlockPos.POS);
		//
		//		TileFarm farm = TileUtil.getTile(world, pos, TileFarm.class);
		EnumFarmBlockTexture texture = EnumFarmBlockTexture.BRICK;
		EnumFarmBlockType type = EnumFarmBlockType.PLAIN;

		//		if (farm != null) {
		//			texture = farm.getFarmBlockTexture();
		//			type = farm.getFarmBlockType();
		//		}

		return new Key(texture, type);
	}

	@Override
	protected void bakeBlock(BlockFarm blockFarm, IModelData extraData, Key key, ModelBaker baker, boolean inventory) {
		TextureAtlasSprite[] textures = getSprites(key.texture);

		// Add the plain block.
		baker.addBlockModel(null, textures, 0);
		// Add the overlay block.
		baker.addBlockModel(null, getOverlaySprites(key.type), 0);

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

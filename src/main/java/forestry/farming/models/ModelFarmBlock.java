package forestry.farming.models;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.BlockFarm.State;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.features.FarmingBlocks;

@OnlyIn(Dist.CLIENT)
public class ModelFarmBlock extends ModelBlockCached<BlockFarm, Pair<BlockFarm, BlockFarm.State>> {

	public ModelFarmBlock() {
		super(BlockFarm.class);
	}

	@Override
	protected Pair<BlockFarm, State> getInventoryKey(ItemStack stack) {
		Block block = Block.byItem(stack.getItem());
		if (block instanceof BlockFarm) {
			return Pair.of((BlockFarm) block, State.PLAIN);
		}
		return Pair.of(FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block(), State.PLAIN);
	}

	@Override
	protected Pair<BlockFarm, State> getWorldKey(BlockState state, ModelData extraData) {
		Block block = state.getBlock();
		if (block instanceof BlockFarm) {
			return Pair.of((BlockFarm) block, state.getValue(BlockFarm.STATE));
		}
		return Pair.of(FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block(), State.PLAIN);
	}

	@Override
	protected void bakeBlock(BlockFarm blockFarm, ModelData extraData, Pair<BlockFarm, State> key, ModelBaker baker, boolean inventory) {
		EnumFarmBlockType type = key.getLeft().getType();
		EnumFarmMaterial material = key.getLeft().getFarmMaterial();
		TextureAtlasSprite[] textures = material.getSprites();

		// Add the plain block.
		baker.addBlockModel(textures, 0);

		// Add the overlay block.
		baker.addBlockModel(type.getSprites(), 0);

		//Add band if plain block, since this is the only farm block type that can be used on layer 2
		if(type == EnumFarmBlockType.PLAIN && key.getRight() == State.BAND) {
			baker.addBlockModel(EnumFarmBlockType.getBandSprites(), 0);
		}

		// Set the particle sprite
		baker.setParticleSprite(textures[0]);
	}

}

package forestry.arboriculture.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import forestry.arboriculture.blocks.BlockForestryDoor;

import org.jetbrains.annotations.Nullable;

public class ItemBlockWoodDoor extends ItemBlockWood<BlockForestryDoor> {

	public ItemBlockWoodDoor(BlockForestryDoor block) {
		super(block);
	}

	/**
	 * Copy of {@link net.minecraft.item.TallBlockItem#placeBlock(BlockItemUseContext, BlockState)}
	 */
	@Override
	protected boolean placeBlock(BlockPlaceContext p_195941_1_, BlockState p_195941_2_) {
		p_195941_1_.getLevel().setBlock(p_195941_1_.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
		return super.placeBlock(p_195941_1_, p_195941_2_);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		if (getBlock().isFireproof()) {
			return 0;
		} else {
			return 200;
		}
	}
}

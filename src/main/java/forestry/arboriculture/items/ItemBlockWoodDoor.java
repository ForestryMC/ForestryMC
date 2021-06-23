package forestry.arboriculture.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;

import forestry.arboriculture.blocks.BlockForestryDoor;

public class ItemBlockWoodDoor extends ItemBlockWood<BlockForestryDoor> {

	public ItemBlockWoodDoor(BlockForestryDoor block) {
		super(block);
	}

	/**
	 * Copy of {@link net.minecraft.item.TallBlockItem#placeBlock(BlockItemUseContext, BlockState)}
	 */
	@Override
	protected boolean placeBlock(BlockItemUseContext p_195941_1_, BlockState p_195941_2_) {
		p_195941_1_.getLevel().setBlock(p_195941_1_.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
		return super.placeBlock(p_195941_1_, p_195941_2_);
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		if (getBlock().isFireproof()) {
			return 0;
		} else {
			return 200;
		}
	}
}

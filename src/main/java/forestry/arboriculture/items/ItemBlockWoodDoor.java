package forestry.arboriculture.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;

import forestry.arboriculture.blocks.BlockForestryDoor;

//TODO eg    public static final Item OAK_DOOR = register(new TallBlockItem(Blocks.OAK_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
public class ItemBlockWoodDoor extends ItemBlockWood<BlockForestryDoor> {

	public ItemBlockWoodDoor(BlockForestryDoor block) {
		super(block);
	}

	/**
	 * Copy of {@link net.minecraft.item.TallBlockItem#placeBlock(BlockItemUseContext, BlockState)}
	 *
	 */
	@Override
	protected boolean placeBlock(BlockItemUseContext p_195941_1_, BlockState p_195941_2_) {
		p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos().up(), Blocks.AIR.getDefaultState(), 27);
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

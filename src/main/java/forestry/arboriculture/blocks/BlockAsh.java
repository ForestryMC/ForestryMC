package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

import net.minecraftforge.common.ToolType;

public class BlockAsh extends Block {

	public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 63);

	public BlockAsh() {
		super(Block.Properties.create(Material.EARTH, MaterialColor.BLACK).sound(SoundType.SAND).harvestTool(ToolType.SHOVEL).harvestLevel(0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AMOUNT);
	}

	//TODO maybe loot table
	//	@Override
	//	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	//		Random rand = world instanceof World ? ((World) world).rand : new Random();
	//		int amount = startAmount + state.get(AMOUNT);
	//		if (amount > 0) {
	//			if (fortune > 0) {
	//				amount += rand.nextInt(1 + fortune);
	//			}
	//			drops.add(new ItemStack(Items.CHARCOAL, amount));
	//			drops.add(new ItemStack(ModuleCore.getItems().ash, 1 + rand.nextInt(amount / 4)));
	//		}
	//	}
}

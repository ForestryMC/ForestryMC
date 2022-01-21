package forestry.arboriculture.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraftforge.common.ToolType;

public class BlockAsh extends Block {

	public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 63);

	public BlockAsh() {
		super(Block.Properties.of(Material.DIRT, MaterialColor.COLOR_BLACK).sound(SoundType.SAND).strength(0.6F).harvestTool(ToolType.SHOVEL).harvestLevel(0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AMOUNT);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
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

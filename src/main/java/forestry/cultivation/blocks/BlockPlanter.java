package forestry.cultivation.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.blocks.BlockBase;
import forestry.core.render.ParticleRender;
import forestry.cultivation.tiles.TilePlanter;

public class BlockPlanter extends BlockBase<BlockTypePlanter> {
	public static final BooleanProperty MANUAL = BooleanProperty.create("manual");

	public BlockPlanter(BlockTypePlanter blockType) {
		super(blockType, Material.WOOD);
		this.setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(MANUAL, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, MANUAL);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (blockType == BlockTypePlanter.FARM_ENDER) {
			for (int i = 0; i < 3; ++i) {
				ParticleRender.addPortalFx(worldIn, pos, rand);
			}
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		TileEntity tile = super.createTileEntity(state, world);
		if (tile instanceof TilePlanter) {
			TilePlanter planter = (TilePlanter) tile;
			planter.setManual(state.get(MANUAL));
		}
		return tile;
	}

	//	@Override
	//	public int damageDropped(BlockState state) {
	//		return state.get(MANUAL) ? 1 : 0;
	//	}
	//
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public void registerStateMapper() {
	//		ModelLoader.setCustomStateMapper(this, new PlanterStateMapper());
	//	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand) {
		return getDefaultState().with(MANUAL, state2.get(MANUAL));    //TODO idk if this is correct
	}

	@Override
	public void fillItemGroup(ItemGroup itemIn, NonNullList<ItemStack> items) {
		for (byte i = 0; i < 2; i++) {
			items.add(get(i == 1));
		}
	}

	//	public static boolean isManual(ItemStack stack) {
	//		return stack.getMetadata() == 1;
	//	}

	public ItemStack get(boolean isManual) {
		return new ItemStack(this);//TODO how to handle this, 1, isManual ? 1 : 0);
	}
}

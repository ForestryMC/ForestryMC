/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.config.Constants;

/**
 * bog earth, which becomes peat
 */
public class BlockBogEarth extends Block implements IItemModelRegister {
	private static final int maturityDelimiter = 3; //maturity at which bogEarth becomes peat
	public static final IntegerProperty MATURITY = IntegerProperty.create("maturity", 0, maturityDelimiter);

	public enum SoilType implements IStringSerializable {
		BOG_EARTH("bog_earth"),
		PEAT("peat");


		private final String name;

		SoilType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public static SoilType fromMaturity(int maturity) {
			if (maturity >= maturityDelimiter) {
				return PEAT;
			} else {
				return BOG_EARTH;
			}
		}
	}

	public BlockBogEarth() {
		super(Block.Properties.create(Material.EARTH)
			.tickRandomly()
			.hardnessAndResistance(0.5f)
			.sound(SoundType.GROUND));
		//TODO - creative tabs done by item. Handle this in per module registries I think
		//setCreativeTab(CreativeTabForestry.tabForestry);

		setDefaultState(this.getStateContainer().getBaseState().with(MATURITY, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(MATURITY);
	}

	@Override
	public int tickRate(IWorldReader world) {
		return 500;
	}

	//TODO - loot tables for drops (at least for easy cases like this
	//	@Override
	//	public List<ItemStack> getDrops(BlockState state, ServerWorld world, BlockPos pos, TileEntity te) {
	//		Integer maturity = state.getComb(MATURITY);
	//		SoilType type = SoilType.fromMaturity(maturity);
	//
	//		if (type == SoilType.PEAT) {
	//			drops.add(ModuleCore.getItems().peat.getItemStack(2));
	//			drops.add(new ItemStack(Blocks.DIRT));
	//		} else {
	//			drops.add(new ItemStack(this, 1, SoilType.BOG_EARTH.ordinal()));
	//		}
	//		return new ArrayList<>();
	//	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.isRemote || world.rand.nextInt(13) != 0) {
			return;
		}

		Integer maturity = state.get(MATURITY);
		SoilType type = SoilType.fromMaturity(maturity);

		if (type == SoilType.BOG_EARTH) {
			if (isMoistened(world, pos)) {
				world.setBlockState(pos, state.with(MATURITY, maturity + 1), Constants.FLAG_BLOCK_SYNC);
			}
		}
	}

	private static boolean isMoistened(World world, BlockPos pos) {
		for (BlockPos waterPos : BlockPos.getAllInBoxMutable(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
			BlockState blockState = world.getBlockState(waterPos);
			Block block = blockState.getBlock();
			if (block == Blocks.WATER) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		return false;
	}

	//TODO - loot tables
	//	@Override
	//	public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, PlayerEntity player) {
	//		return false;
	//	}

	public static SoilType getTypeFromState(BlockState state) {
		Integer maturity = state.get(MATURITY);
		return SoilType.fromMaturity(maturity);
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1));
	}

	//TODO still needed?
	/* MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "soil/bog");
		manager.registerItemModel(item, 1, "soil/bog");
		manager.registerItemModel(item, 2, "soil/bog");
		manager.registerItemModel(item, 3, "soil/peat");
	}

	//TODO - loot tables or flatten
	//	@Override
	//	public int damageDropped(BlockState state) {
	//		return 0;
	//	}

	public ItemStack get(SoilType soilType, int amount) {    //TODO- check
		return new ItemStack(this.getStateContainer().getBaseState().with(MATURITY, soilType.ordinal()).getBlock(), amount);
	}
}

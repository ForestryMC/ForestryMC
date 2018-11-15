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
package forestry.lepidopterology.blocks;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.tiles.TileUtil;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.tiles.TileCocoon;

public class BlockCocoon extends Block implements ITileEntityProvider, IStateMapperRegister, IItemModelRegister {
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.3125F, 0.3125F, 0.3125F, 0.6875F, 1F, 0.6875F);
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;

	public BlockCocoon() {
		super(MaterialCocoon.INSTANCE);
		setTickRandomly(true);
		setSoundType(SoundType.GROUND);
		setCreativeTab(null);
		setDefaultState(this.blockState.getBaseState().withProperty(COCOON, ButterflyAlleles.cocoonDefault)
			.withProperty(AlleleButterflyCocoon.AGE, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COCOON, AlleleButterflyCocoon.AGE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (cocoon != null) {
			state = state.withProperty(COCOON, cocoon.getCaterpillar().getGenome().getCocoon())
				.withProperty(AlleleButterflyCocoon.AGE, cocoon.getAge());
		}
		return super.getActualState(state, world, pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new CocoonStateMapper());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		// To delete the error message
		manager.registerItemModel(item, 0, "cocoon_late");
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		TileCocoon tileCocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tileCocoon == null) {
			return;
		}

		if (tileCocoon.isInvalid()) {
			return;
		}

		tileCocoon.onBlockTick();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCocoon(false);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isAirBlock(pos.up())) {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
		EntityPlayer player) {
		TileCocoon tile = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tile == null) {
			return ItemStack.EMPTY;
		}

		IButterfly caterpillar = tile.getCaterpillar();
		int age = tile.getAge();

		ItemStack stack = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.COCOON);
		if (!stack.isEmpty() && stack.getTagCompound() != null) {
			stack.getTagCompound().setInteger(ItemButterflyGE.NBT_AGE, age);
		}
		return stack;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return BOUNDING_BOX.offset(pos);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}

}

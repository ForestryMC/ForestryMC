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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IStateMapperRegister;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.lepidopterology.blocks.property.PropertyCocoon;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.tiles.TileCocoon;

public class BlockCocoon extends Block implements ITileEntityProvider, IStateMapperRegister {
	
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;
	
	public BlockCocoon() {
		super(new MaterialCocoon());
		setTickRandomly(true);
		setStepSound(soundTypeGrass);
		setCreativeTab(null);
		setDefaultState(this.blockState.getBaseState().withProperty(COCOON, AlleleButterflyCocoon.cocoonDefault).withProperty(AlleleButterflyCocoon.AGE, 0));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, COCOON, AlleleButterflyCocoon.AGE);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if(cocoon != null){
			state = state.withProperty(COCOON, cocoon.getCaterpillar().getGenome().getCocoon()).withProperty(AlleleButterflyCocoon.AGE, cocoon.getAge());
		}
		return super.getActualState(state, world, pos);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new CocoonStateMapper());
	}
	
    @Override
	public boolean isFullCube(){
        return false;
    }

    @Override
	public boolean isOpaqueCube(){
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

		if (world.rand.nextFloat() > 0.1) {
			return;
		}
		tileCocoon.onBlockTick();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCocoon(false);
	}

    @Override
	public int getMetaFromState(IBlockState state){
        return 0;
    }
    
    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
    	IBlockState stateUp = worldIn.getBlockState(pos.up());
    	if(stateUp.getBlock().isAir(worldIn, pos.up())){
    		worldIn.setBlockToAir(pos);
    	}
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    	return Collections.emptyList();
    }

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		TileCocoon tile = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tile == null) {
			return null;
		}

		IButterfly caterpillar = tile.getCaterpillar();
		int age = tile.getAge();

		ItemStack stack = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.COCOON);
		if (stack == null) {
			return null;
		}

		stack.getTagCompound().setInteger(ItemButterflyGE.NBT_AGE, age);

		return stack;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
    	setBlockBounds(0.3125F, 0.3125F, 0.3125F, 0.6875F, 1F, 0.6875F);
    	return super.getSelectedBoundingBox(world, pos);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
    	setBlockBounds(0.3125F, 0.3125F, 0.3125F, 0.6875F, 1F, 0.6875F);
    	return super.getCollisionBoundingBox(world, pos, state);
    }
    
}

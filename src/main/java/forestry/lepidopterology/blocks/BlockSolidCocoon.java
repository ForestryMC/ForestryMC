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
import forestry.api.core.IStateMapperRegister;
import forestry.core.proxy.Proxies;
import forestry.core.render.EmptyStateMapper;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.blocks.property.PropertyCocoon;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSolidCocoon extends Block implements ITileEntityProvider, IStateMapperRegister {
	
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;
	
	public BlockSolidCocoon() {
		super(new MaterialCocoon());
		setHarvestLevel("scoop", 0);
		setHardness(0.5F);
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
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (canHarvestBlock(world, pos, player)) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof TileCocoon) {
				TileCocoon cocoon = (TileCocoon) tile;
				ItemStack[] drops = cocoon.getCocoonDrops();
				if (drops != null) {
					for (ItemStack stack : drops) {
						if (stack != null) {
							ItemStackUtil.dropItemStackAsEntity(stack, world, pos);
						}
					}
				}
			}
		}

		return world.setBlockToAir(pos);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCocoon(true);
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
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
    	TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
    	if(cocoon != null){
    		if(cocoon.getAge() == 0){
    			setBlockBounds(0.375F, 0.5F, 0.4375F, 0.5625F, 1F, 0.6875F);
    		}else if(cocoon.getAge() == 1){
    			setBlockBounds(0.34375F, 0.375F, 0.40625F, 0.59375F, 1F, 0.71875F);
    		}else if(cocoon.getAge() == 2){
    			setBlockBounds(0.34375F, 0.25F, 0.28125F, 0.71875F, 1F, 0.71875F);
    		}else{
    			setBlockBounds(0, 0, 0, 1, 1, 1);
    		}
    	}
    	return super.getSelectedBoundingBox(world, pos);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
    	TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
    	if(cocoon != null){
    		if(cocoon.getAge() == 0){
    			setBlockBounds(0.375F, 0.5F, 0.4375F, 0.5625F, 1F, 0.6875F);
    		}else if(cocoon.getAge() == 1){
    			setBlockBounds(0.34375F, 0.375F, 0.40625F, 0.59375F, 1F, 0.71875F);
    		}else if(cocoon.getAge() == 2){
    			setBlockBounds(0.34375F, 0.25F, 0.28125F, 0.71875F, 1F, 0.71875F);
    		}else{
    			setBlockBounds(0, 0, 0, 1, 1, 1);
    		}
    	}
    	return super.getCollisionBoundingBox(world, pos, state);
    }
    
}

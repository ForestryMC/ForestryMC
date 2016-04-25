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

import forestry.api.core.IStateMapperRegister;
import forestry.core.proxy.Proxies;
import forestry.core.render.EmptyStateMapper;
import forestry.core.tiles.TileUtil;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCocoon extends Block implements ITileEntityProvider, IStateMapperRegister {
	
	public BlockCocoon() {
		super(new MaterialCocoon());
		setTickRandomly(true);
		setStepSound(soundTypeGrass);
		setCreativeTab(null);
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
    public int getRenderType() {
    	return 2;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerStateMapper() {
    	Proxies.render.registerStateMapper(this, EmptyStateMapper.instance);
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
    
}

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
import forestry.api.core.Tabs;
import forestry.core.proxy.Proxies;
import forestry.core.render.EmptyStateMapper;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSolidCocoon extends Block implements ITileEntityProvider, IStateMapperRegister {
	
	public BlockSolidCocoon() {
		super(Material.cloth);
		setHarvestLevel("scoop", 0);
		setHardness(0.5F);
		setTickRandomly(true);
		setStepSound(soundTypeGrass);
		setCreativeTab(Tabs.tabLepidopterology);
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
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
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

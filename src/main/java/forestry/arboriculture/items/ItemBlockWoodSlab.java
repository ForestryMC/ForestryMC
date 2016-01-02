package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.blocks.BlockSlab;
import forestry.arboriculture.blocks.BlockSlabDouble;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.tiles.TileUtil;

public class ItemBlockWoodSlab extends ItemBlockWood {
	private final BlockSlab slab;
	private final BlockSlab doubleSlab;

	public ItemBlockWoodSlab(Block block, BlockSlabDouble doubleSlab, BlockSlab slab) {
		super(block);
		this.doubleSlab = doubleSlab;
		this.slab = slab;
	}
	
	@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos);
            
			EnumWoodType blockWoodType = null;
			EnumWoodType stackWoodType = getWoodType(stack);
			TileWood tile = TileUtil.getTile(world, pos, TileWood.class);
			if (tile != null) {
				blockWoodType = tile.getWoodType();
			}

            if (iblockstate.getBlock() == this.slab){
                EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(net.minecraft.block.BlockSlab.HALF);

                if ((side == EnumFacing.UP && blockslab$enumblockhalf == EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && blockslab$enumblockhalf == EnumBlockHalf.TOP) && blockWoodType == stackWoodType)
                {
                    if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(world, pos, iblockstate)) )
                    {
                    	if(placeWood(stack, stackWoodType, doubleSlab, player, world, pos, doubleSlab.getDefaultState())){
	                        world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
	                        --stack.stackSize;
                    	}
                    }

                    return true;
                }
            }

            return this.tryPlace(stack, world, pos.offset(side), stackWoodType) ? true : super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack itemStack) {
        BlockPos blockpos = pos;
        IBlockState iblockstate = world.getBlockState(pos);
        
		EnumWoodType blockWoodType = null;
		EnumWoodType stackWoodType = getWoodType(itemStack);
		TileWood tile = TileUtil.getTile(world, pos, TileWood.class);
		if (tile != null) {
			blockWoodType = tile.getWoodType();
		}

        if (iblockstate.getBlock() == slab)
        {
            boolean flag = iblockstate.getValue(net.minecraft.block.BlockSlab.HALF) == EnumBlockHalf.TOP;

            if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && stackWoodType == blockWoodType)
            {
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState iblockstate1 = world.getBlockState(pos);
        return iblockstate1.getBlock() == this.slab && stackWoodType == blockWoodType ? true : super.canPlaceBlockOnSide(world, blockpos, side, player, itemStack);
	}
	
    private boolean tryPlace(ItemStack itemStack, World world, BlockPos pos, Object variantInStack)
    {
        IBlockState iblockstate = world.getBlockState(pos);

		EnumWoodType blockWoodType = null;
		TileWood tile = TileUtil.getTile(world, pos, TileWood.class);
		if (tile != null) {
			blockWoodType = tile.getWoodType();
		}
        
        if (iblockstate.getBlock() == slab)
        {
            if (blockWoodType == variantInStack)
            {
                if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(world, pos, doubleSlab.getDefaultState())))
                {
                	if(placeWood(itemStack, (EnumWoodType) variantInStack, doubleSlab, null, world, pos, doubleSlab.getDefaultState())){
                		world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
                    	--itemStack.stackSize;
                	}
                }

                return true;
            }
        }

        return false;
    }
}

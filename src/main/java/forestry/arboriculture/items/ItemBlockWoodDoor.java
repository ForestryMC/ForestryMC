package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.arboriculture.blocks.BlockArbDoor;

public class ItemBlockWoodDoor extends ItemBlockWood<BlockArbDoor> {

	public ItemBlockWoodDoor(Block block) {
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side != EnumFacing.UP) {
			return false;
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();

			if (!block.isReplaceable(worldIn, pos)) {
				pos = pos.offset(side);
			}

			if (!playerIn.canPlayerEdit(pos, side, stack)) {
				return false;
			} else if (!this.block.canPlaceBlockAt(worldIn, pos)) {
				return false;
			} else {
				ItemDoor.placeDoor(worldIn, pos, EnumFacing.fromAngle((double) playerIn.rotationYaw), this.block);
				--stack.stackSize;
				return true;
			}
		}
	}
}

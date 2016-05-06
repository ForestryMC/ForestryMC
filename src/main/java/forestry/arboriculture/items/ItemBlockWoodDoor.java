package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.arboriculture.blocks.BlockArbDoor;

public class ItemBlockWoodDoor extends ItemBlockWood<BlockArbDoor> {

	public ItemBlockWoodDoor(Block block) {
		super(block);
	}

	/** Copy of {@link ItemDoor#onItemUse} */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();

			if (!block.isReplaceable(worldIn, pos)) {
				pos = pos.offset(facing);
			}

			if (playerIn.canPlayerEdit(pos, facing, stack) && this.block.canPlaceBlockAt(worldIn, pos)) {
				EnumFacing enumfacing = EnumFacing.fromAngle(playerIn.rotationYaw);
				int x = enumfacing.getFrontOffsetX();
				int z = enumfacing.getFrontOffsetZ();
				boolean isRightHinge = x < 0 && hitZ < 0.5F || x > 0 && hitZ > 0.5F || z < 0 && hitX > 0.5F || z > 0 && hitX < 0.5F;
				ItemDoor.placeDoor(worldIn, pos, enumfacing, this.block, isRightHinge);
				SoundType soundtype = this.block.getSoundType();
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				--stack.stackSize;
				return EnumActionResult.SUCCESS;
			} else {
				return EnumActionResult.FAIL;
			}
		}
	}
}

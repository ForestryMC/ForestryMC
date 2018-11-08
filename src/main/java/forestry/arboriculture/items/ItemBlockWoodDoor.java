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

	public ItemBlockWoodDoor(BlockArbDoor block) {
		super(block);
	}

	/**
	 * Copy of {@link ItemDoor#onItemUse}
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();

			if (!block.isReplaceable(worldIn, pos)) {
				pos = pos.offset(facing);
			}

			ItemStack itemstack = player.getHeldItem(hand);

			if (player.canPlayerEdit(pos, facing, itemstack) && this.block.canPlaceBlockAt(worldIn, pos)) {
				EnumFacing enumfacing = EnumFacing.fromAngle(player.rotationYaw);
				int i = enumfacing.getXOffset();
				int j = enumfacing.getZOffset();
				boolean flag = i < 0 && hitZ < 0.5F || i > 0 && hitZ > 0.5F || j < 0 && hitX > 0.5F || j > 0 && hitX < 0.5F;
				ItemDoor.placeDoor(worldIn, pos, enumfacing, this.block, flag);
				SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				itemstack.shrink(1);
				return EnumActionResult.SUCCESS;
			} else {
				return EnumActionResult.FAIL;
			}
		}
	}

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		if (getBlock().isFireproof()) {
			return 0;
		} else {
			return 200;
		}
	}
}

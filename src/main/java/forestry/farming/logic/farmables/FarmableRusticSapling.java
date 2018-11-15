package forestry.farming.logic.farmables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableRusticSapling implements IFarmable {

	protected final Item germling;
	protected final Block germlingBlock;
	private final ItemStack[] windfall;

	public FarmableRusticSapling(Item germling, ItemStack[] windfall) {
		this.germling = germling;
		this.germlingBlock = Block.getBlockFromItem(germling);
		this.windfall = windfall;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		IBlockState blockState = germlingBlock.getStateFromMeta(germling.getItemDamage());    //TODO - stop using meta here
		if (world.setBlockState(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return blockState.getBlock() == germlingBlock;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		if (!block.isWood(world, pos)) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return germling == itemstack.getItem();
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addGermlings(new ItemStack(germling));
		info.addProducts(windfall);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall) {
			if (drop.isItemEqual(itemstack)) {
				return true;
			}
		}
		return false;
	}
}

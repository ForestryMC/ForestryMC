package forestry.farming.logic.farmables;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
		this.germlingBlock = Block.byItem(germling);
		this.windfall = windfall;
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		BlockState blockState = germlingBlock.defaultBlockState();//TODO flatten germlingBlock.getStateFromMeta(germling.getItemDamage());    //TODO - stop using meta here
		if (world.setBlockAndUpdate(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	@Override
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == germlingBlock;
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		Block block = blockState.getBlock();
		if (false) {//TODO tags !block.isWood(world, pos)) {
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
		info.addSeedlings(new ItemStack(germling));
		info.addProducts(windfall);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall) {
			if (drop.sameItem(itemstack)) {
				return true;
			}
		}
		return false;
	}
}

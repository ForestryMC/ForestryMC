package forestry.farming.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

import forestry.plugins.compat.deprecated.PluginIC2;

public class CropBasicIC2Crop extends Crop {
	private final TileEntity tileEntity;

	public CropBasicIC2Crop(World world, TileEntity tileEntity, BlockPos position) {
		super(world, position);
		this.tileEntity = tileEntity;
	}

	@Override
	protected boolean isCrop(BlockPos pos) {
		return PluginIC2.instance.canHarvestCrop(this.tileEntity);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(BlockPos pos) {
		return PluginIC2.instance.getCropDrops(this.tileEntity);
	}
}
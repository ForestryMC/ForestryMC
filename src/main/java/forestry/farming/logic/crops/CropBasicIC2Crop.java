package forestry.farming.logic.crops;


import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import forestry.plugins.PluginIC2;

import ic2.api.crops.ICropTile;

public class CropBasicIC2Crop extends Crop {
	private final TileEntity tileEntity;

	public CropBasicIC2Crop(@Nonnull World world, @Nonnull TileEntity tileEntity, @Nonnull BlockPos position) {
		super(world, position);
		this.tileEntity = tileEntity;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return canHarvestCrop(this.tileEntity);
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		return getCropDrops(this.tileEntity);
	}

	/**
	 * Check if there is an instance of ICropTile.
	 *
	 * @param tileEntity tile entity to be checked.
	 * @return true if there is an IC2 crop and false otherwise.
	 */
	@Optional.Method(modid = PluginIC2.MOD_ID)
	public static boolean isIC2Crop(TileEntity tileEntity) {
		return tileEntity instanceof ICropTile;
	}

	/**
	 * Check if an IC2 crop is ready to be harvested.
	 *
	 * @param tileEntity tile entity to be checked.
	 * @return true if crop size is optimal for harvest and false otherwise.
	 */
	@Optional.Method(modid = PluginIC2.MOD_ID)
	public static boolean canHarvestCrop(TileEntity tileEntity) {
		if (isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile) tileEntity;
			if (crop.getCrop() == null) {
				return false;
			}
			return crop.getCurrentSize() == crop.getCrop().getOptimalHarvestSize(crop);
		}
		return false;
	}

	/**
	 * This function takes care of everything related to the harvesting of the
	 * crop meaning it will calculate the drops and also do setSizeAfterHarvest().
	 *
	 * @param tileEntity tile entity to be checked.
	 * @return list containing the drops.
	 */
	@Optional.Method(modid = PluginIC2.MOD_ID)
	private static NonNullList<ItemStack> getCropDrops(TileEntity tileEntity) {
		if (isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile) tileEntity;
			NonNullList<ItemStack> drops = NonNullList.create();
			drops.addAll(crop.performHarvest());
			return drops;
		}
		return null;
	}
}

package forestry.farming.logic;

// TODO: IC2 for 1.9
public class FarmableBasicIC2Crop {//implements IFarmable {

//	/**
//	 * Perform some of the actions of the crop-matron.
//	 */
//	@Optional.Method(modid = PluginIC2.modId)
//	public static void babysitCrop(TileEntity tileEntity) {
//		if (CropBasicIC2Crop.isIC2Crop(tileEntity)) {
//			ICropTile crop = (ICropTile) tileEntity;
//			/*
//			This part might be unbalanced until a custom farm logic is added and makes use of weed-ex.
//			if (crop.getCrop() != null) {
//				if (crop.getCrop().isWeed(crop)) {
//					crop.reset();
//				}
//			}*/
//			if (crop.getStorageWater() <= 200) {
//				crop.setStorageWater(200);
//			}
//			if (crop.getStorageNutrient() <= 100) {
//				crop.setStorageNutrient(crop.getStorageNutrient() + 100);
//			}
//		}
//	}
//
//	@Override
//	public boolean isSaplingAt(World world, BlockPos pos) {
//		TileEntity crop = world.getTileEntity(pos);
//		if (CropBasicIC2Crop.isIC2Crop(crop)) {
//			babysitCrop(crop);
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public ICrop getCropAt(World world, BlockPos pos) {
//		TileEntity crop = world.getTileEntity(pos);
//		if (crop == null) {
//			return null;
//		}
//		if (!CropBasicIC2Crop.isIC2Crop(crop)) {
//			return null;
//		}
//		if (!CropBasicIC2Crop.canHarvestCrop(crop)) {
//			return null;
//		}
//		return new CropBasicIC2Crop(world, crop, new Vect(pos));
//	}
//
//	@Override
//	public boolean isGermling(ItemStack itemstack) {
//		return false;
//	}
//
//	@Override
//	public boolean isWindfall(ItemStack itemstack) {
//		return false;
//	}
//
//	@Override
//	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
//		return false;
//	}
}

package forestry.core;

import org.apache.commons.lang3.tuple.Pair;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;

public class CamouflageHandlerNone implements ICamouflageItemHandler {

	@Override
	public boolean canHandle(ItemStack stack) {
		return true;
	}

	@Override
	public String getType() {
		return CamouflageManager.NONE;
	}

	@Override
	public float getLightTransmittance(ItemStack stack, ICamouflageHandler camouflageHandler) {
		return 0;
	}

	@Override
	public Pair<IBlockState, IBakedModel> getModel(ItemStack stack, ICamouflageHandler camouflageHandler, ICamouflagedTile camouflageTile) {
		return Pair.of(null, null);
	}

}

/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public interface ICamouflageItemHandler {

	boolean canHandle(ItemStack stack);

	String getType();

	float getLightTransmittance(ItemStack stack, ICamouflageHandler camouflageHandler);

	@SideOnly(Side.CLIENT)
	Pair<IBlockState, IBakedModel> getModel(ItemStack stack, ICamouflageHandler camouflageHandler, ICamouflagedTile camouflageTile);

}

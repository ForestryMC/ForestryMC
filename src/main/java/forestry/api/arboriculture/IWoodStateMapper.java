/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//import net.minecraft.client.renderer.block.statemap.IStateMapper;
@OnlyIn(Dist.CLIENT)
public interface IWoodStateMapper {//extends IStateMapper {

	ModelResourceLocation getModelLocation(BlockState state);

	ModelResourceLocation getDefaultModelResourceLocation(BlockState state);

}

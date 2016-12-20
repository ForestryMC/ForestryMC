/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.render;

import java.util.Map;

import forestry.api.arboriculture.EnumPileType;
import forestry.arboriculture.PluginArboriculture;
import forestry.core.config.Constants;
import forestry.core.render.ForestryStateMapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PileStateMapper extends ForestryStateMapper {

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		String location = Constants.MOD_ID + ":pile";

		IBlockState woodPileState = PluginArboriculture.blocks.piles.get(EnumPileType.WOOD).getDefaultState();
		mapStateModelLocations.put(woodPileState, new ModelResourceLocation(location, "type=wood"));

		IBlockState dirtPileState = PluginArboriculture.blocks.piles.get(EnumPileType.DIRT).getDefaultState();
		mapStateModelLocations.put(dirtPileState, new ModelResourceLocation(location, "type=dirt"));

		IBlockState ashPileState = PluginArboriculture.blocks.piles.get(EnumPileType.ASH).getDefaultState();
		mapStateModelLocations.put(ashPileState, new ModelResourceLocation(location, "type=ash"));

		return this.mapStateModelLocations;
	}

}

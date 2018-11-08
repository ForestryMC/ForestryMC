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
package forestry.core.genetics.mutations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;

public class MutationConditionRequiresResource implements IMutationCondition {

	private final Set<IBlockState> acceptedBlockStates = new HashSet<>();
	private final String displayName;

	public MutationConditionRequiresResource(String oreDictName) {
		this.displayName = oreDictName;
		for (ItemStack ore : OreDictionary.getOres(oreDictName)) {
			if (!ore.isEmpty()) {
				Item oreItem = ore.getItem();
				Block oreBlock = Block.getBlockFromItem(oreItem);
				if (oreBlock != Blocks.AIR) {
					this.acceptedBlockStates.addAll(oreBlock.getBlockState().getValidStates());
				}
			}
		}
	}

	public MutationConditionRequiresResource(IBlockState... acceptedBlockStates) {
		Collections.addAll(this.acceptedBlockStates, acceptedBlockStates);
		this.displayName = acceptedBlockStates[0].getBlock().getLocalizedName();
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		TileEntity tile;
		do {
			pos = pos.down();
			tile = TileUtil.getTile(world, pos);
		} while (tile instanceof IBeeHousing);

		IBlockState blockState = world.getBlockState(pos);
		return this.acceptedBlockStates.contains(blockState) ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocalFormatted("for.mutation.condition.resource", displayName);
	}
}

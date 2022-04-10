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

import java.util.Arrays;
import java.util.List;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IMutationCondition;
import forestry.core.tiles.TileUtil;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class MutationConditionRequiresResource implements IMutationCondition {

	private final List<BlockState> acceptedBlockStates;

	public MutationConditionRequiresResource(BlockState... acceptedBlockStates) {
		this.acceptedBlockStates = Arrays.asList(acceptedBlockStates); // TODO: Defensive copy?
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		TileEntity tile;
		do {
			pos = pos.below();
			tile = TileUtil.getTile(world, pos);
		} while (tile instanceof IBeeHousing);

		BlockState blockState = world.getBlockState(pos);
		return this.acceptedBlockStates.contains(blockState) ? 1 : 0;
	}

	@Override
	public ITextComponent getDescription() {
		if (acceptedBlockStates.isEmpty()) {
			return NarratorChatListener.NO_TITLE;
		} else {
			return new TranslationTextComponent("for.mutation.condition.resource", acceptedBlockStates.get(0).getBlock().getName());
		}
	}
}

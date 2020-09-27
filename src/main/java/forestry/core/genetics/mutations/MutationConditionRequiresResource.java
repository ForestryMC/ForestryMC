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

import forestry.api.apiculture.IBeeHousing;
import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IMutationCondition;
import forestry.core.tiles.TileUtil;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MutationConditionRequiresResource implements IMutationCondition {

    private final Set<BlockState> acceptedBlockStates = new HashSet<>();
    private final String displayName;

    public MutationConditionRequiresResource(String oreDictName) {
        this.displayName = oreDictName;
        for (ItemStack ore : new ItemStack[0]) {//TODO oredictionary OreDictionary.getOres(oreDictName)) {
            if (!ore.isEmpty()) {
                Item oreItem = ore.getItem();
                Block oreBlock = Block.getBlockFromItem(oreItem);
                //TODO - tag or state, Blocks,AIR doesn't cover everything any more
                if (oreBlock != Blocks.AIR) {
                    this.acceptedBlockStates.addAll(oreBlock.getStateContainer().getValidStates());
                }
            }
        }
    }

    public MutationConditionRequiresResource(BlockState... acceptedBlockStates) {
        Collections.addAll(this.acceptedBlockStates, acceptedBlockStates);
        this.displayName = acceptedBlockStates[0].getBlock().getRegistryName().toString();    //TODO translation
    }

    @Override
    public float getChance(
            World world,
            BlockPos pos,
            IAllele allele0,
            IAllele allele1,
            IGenome genome0,
            IGenome genome1,
            IClimateProvider climate
    ) {
        TileEntity tile;
        do {
            pos = pos.down();
            tile = TileUtil.getTile(world, pos);
        } while (tile instanceof IBeeHousing);

        BlockState blockState = world.getBlockState(pos);
        return this.acceptedBlockStates.contains(blockState) ? 1 : 0;
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("for.mutation.condition.resource", displayName);
    }
}

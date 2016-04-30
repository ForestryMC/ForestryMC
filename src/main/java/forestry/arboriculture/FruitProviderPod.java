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
package forestry.arboriculture;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IFruitFamily;
import forestry.core.utils.BlockUtil;

public class FruitProviderPod extends FruitProviderNone {

	public enum EnumPodType {
		COCOA((short) 2000, (short) 2001, (short) 2002), DATES((short) 2010, (short) 2011, (short) 2012),
		PAPAYA((short) 2013, (short) 2014, (short) 2015);//, COCONUT((short)2016, (short)2017, (short)2018);

		public final short[] uids;

		EnumPodType(short stage1, short stage2, short stage3) {
			uids = new short[]{stage1, stage2, stage3};
		}
		
		public String getModelName() {
			return toString().toLowerCase(Locale.ENGLISH);
		}
	}

	private final EnumPodType type;
	@Nonnull
	private final Map<ItemStack, Float> drops;

	public FruitProviderPod(String unlocalizedDescription, IFruitFamily family, EnumPodType type, ItemStack... dropOnMature) {
		super(unlocalizedDescription, family);
		this.type = type;
		this.drops = new HashMap<>();
		for (ItemStack drop : dropOnMature) {
			this.drops.put(drop, 1.0f);
		}
	}

	@Override
	public boolean requiresFruitBlocks() {
		return true;
	}

	@Nonnull
	@Override
	public List<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		if (drops.isEmpty()) {
			return Collections.emptyList();
		}

		if (ripeningTime >= 2) {
			List<ItemStack> drops = new ArrayList<>();
			for (ItemStack aDrop : this.drops.keySet()) {
				drops.add(aDrop.copy());
			}
			return drops;
		}

		return Collections.emptyList();
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, BlockPos pos) {

		if (world.rand.nextFloat() > genome.getSappiness()) {
			return false;
		}

		if (type == EnumPodType.COCOA) {
			return BlockUtil.tryPlantCocoaPod(world, pos);
		} else {
			IAlleleFruit activeAllele = (IAlleleFruit) genome.getActiveAllele(EnumTreeChromosome.FRUITS);
			return TreeManager.treeRoot.setFruitBlock(world, activeAllele, genome.getSappiness(), pos);
		}
	}
	
	@Override
	public short getSpriteIndex(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime, boolean fancy) {
		if (ripeningTime < 0 || ripeningTime >= type.uids.length) {
			return getDecorativeSpriteIndex();
		}
		return type.uids[ripeningTime];
	}

	@Override
	public short getDecorativeSpriteIndex() {
		int index = type.uids.length - 1;
		return type.uids[index];
	}

	@Nonnull
	@Override
	public Map<ItemStack, Float> getProducts() {
		return Collections.unmodifiableMap(drops);
	}

	@Override
	public void registerSprites() {
	}

	@Nonnull
	@Override
	public String getModelName() {
		return type.getModelName();
	}

}

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

import java.util.Locale;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IFruitFamily;
import forestry.core.render.TextureManager;
import forestry.core.utils.BlockUtil;

public class FruitProviderPod extends FruitProviderNone {

	public enum EnumPodType {
		COCOA((short) 2000, (short) 2001, (short) 2002), DATES((short) 2010, (short) 2011,
				(short) 2012), PAPAYA((short) 2013, (short) 2014, (short) 2015);// ,
																				// COCONUT((short)2016,
																				// (short)2017,
																				// (short)2018);

		public final short[] uids;

		EnumPodType(short stage1, short stage2, short stage3) {
			uids = new short[] { stage1, stage2, stage3 };
		}
	}

	private static final ItemStack[] DUMMY = new ItemStack[0];

	private final EnumPodType type;
	private final ItemStack[] drop;

	public FruitProviderPod(String key, IFruitFamily family, EnumPodType type, ItemStack... dropOnMature) {
		super(key, family);
		this.type = type;
		this.drop = dropOnMature;
	}

	@Override
	public boolean requiresFruitBlocks() {
		return true;
	}

	@Override
	public ItemStack[] getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		if (drop == null || drop.length == 0) {
			return DUMMY;
		}

		if (ripeningTime >= 2) {
			ItemStack[] dropping = new ItemStack[drop.length];
			for (int i = 0; i < drop.length; i++) {
				dropping[i] = drop[i].copy();
			}
			return dropping;
		}

		return DUMMY;
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, BlockPos pos) {

		if (world.rand.nextFloat() > genome.getSappiness()) {
			return false;
		}

		if (type == EnumPodType.COCOA) {
			return BlockUtil.tryPlantPot(world, pos, Blocks.cocoa);
		} else {
			return TreeManager.treeRoot.setFruitBlock(world,
					(IAlleleFruit) genome.getActiveAllele(EnumTreeChromosome.FRUITS), genome.getSappiness(), type.uids,
					pos);
		}
	}

	@Override
	public short getIconIndex(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime, boolean fancy) {
		return type.uids[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons() {
		if (type == EnumPodType.COCOA) {
			return;
		}

		TextureManager.getInstance().registerTexUID(type.uids[0], "blocks",
				"pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".0");
		TextureManager.getInstance().registerTexUID(type.uids[1], "blocks",
				"pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".1");
		TextureManager.getInstance().registerTexUID(type.uids[2], "blocks",
				"pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".2");
	}

}

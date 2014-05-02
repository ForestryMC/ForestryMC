/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture;

import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IFruitFamily;
import forestry.core.render.TextureManager;
import forestry.core.utils.BlockUtil;
import forestry.plugins.PluginArboriculture;

public class FruitProviderPod extends FruitProviderNone {

	public static enum EnumPodType {
		COCOA((short) 2000, (short) 2001, (short) 2002), DATES((short) 2010, (short) 2011, (short) 2012),
		PAPAYA((short)2013, (short)2014, (short)2015);//, COCONUT((short)2016, (short)2017, (short)2018);

		public final short[] uids;

		private EnumPodType(short stage1, short stage2, short stage3) {
			uids = new short[] { stage1, stage2, stage3 };
		}
	}

	private static ItemStack[] DUMMY = new ItemStack[0];

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
	public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
		if (drop == null || drop.length == 0)
			return DUMMY;

		if (ripeningTime >= 2) {
			ItemStack[] dropping = new ItemStack[drop.length];
			for (int i = 0; i < drop.length; i++)
				dropping[i] = drop[i].copy();
			return dropping;
		}

		return DUMMY;
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, int x, int y, int z) {

		if (world.rand.nextFloat() > genome.getSappiness())
			return false;

		if (type == EnumPodType.COCOA)
			return BlockUtil.tryPlantPot(world, x, y, z, Blocks.cocoa);
		else {
			return PluginArboriculture.treeInterface.setFruitBlock(world, (IAlleleFruit) genome.getActiveAllele(EnumTreeChromosome.FRUITS.ordinal()),
					genome.getSappiness(), type.uids, x, y, z);
		}
	}

	@Override
	public short getIconIndex(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime, boolean fancy) {
		return type.uids[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		if (type == EnumPodType.COCOA)
			return;

		TextureManager.getInstance().registerTexUID(register, type.uids[0], "pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".0");
		TextureManager.getInstance().registerTexUID(register, type.uids[1], "pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".1");
		TextureManager.getInstance().registerTexUID(register, type.uids[2], "pods/" + type.toString().toLowerCase(Locale.ENGLISH) + ".2");
	}

}

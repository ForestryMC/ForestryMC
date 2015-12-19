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

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IModelManager;
import forestry.api.genetics.IFruitFamily;

public class FruitProviderNone implements IFruitProvider {

	private static class OverlayType {
		public final String ident;
		public final short texUID;

		public OverlayType(String ident, short texUID) {
			this.ident = ident;
			this.texUID = texUID;
		}
	}

	private static final HashMap<String, OverlayType> overlayTypes = new HashMap<>();

	static {
		overlayTypes.put("berries", new OverlayType("berries", (short) 1000));
		overlayTypes.put("pomes", new OverlayType("pomes", (short) 1001));
		overlayTypes.put("nuts", new OverlayType("nuts", (short) 1002));
		overlayTypes.put("citrus", new OverlayType("citrus", (short) 1003));
		overlayTypes.put("plums", new OverlayType("plums", (short) 1004));
	}

	private final String key;
	private final IFruitFamily family;

	protected int ripeningPeriod = 10;

	private OverlayType overlay = null;

	public FruitProviderNone(String key, IFruitFamily family) {
		this.key = key;
		this.family = family;
	}

	public IFruitProvider setOverlay(String ident) {
		overlay = overlayTypes.get(ident);
		return this;
	}

	@Override
	public IFruitFamily getFamily() {
		return family;
	}

	@Override
	public ItemStack[] getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		return new ItemStack[0];
	}

	@Override
	public boolean requiresFruitBlocks() {
		return false;
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, BlockPos pos) {
		return false;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime) {
		return 0xffffff;
	}

	@Override
	public boolean markAsFruitLeaf(ITreeGenome genome, World world, BlockPos pos) {
		return false;
	}

	@Override
	public int getRipeningPeriod() {
		return ripeningPeriod;
	}

	@Override
	public ItemStack[] getProducts() {
		return new ItemStack[0];
	}

	@Override
	public ItemStack[] getSpecialty() {
		return new ItemStack[0];
	}

	@Override
	public String getDescription() {
		return "fruits." + key;
	}

	@Override
	public short getIconIndex(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime, boolean fancy) {
		if (overlay != null) {
			return overlay.texUID;
		} else {
			return -1;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(Item item, IModelManager manager) {
		if (overlay != null) {
			manager.registerItemModel(item, 0, "blocks/leaves/fruits." + overlay.ident);
		}
	}
}

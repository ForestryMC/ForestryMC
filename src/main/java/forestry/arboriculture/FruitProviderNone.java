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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IFruitFamily;
import forestry.core.render.TextureManager;

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
	public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
		return new ItemStack[0];
	}

	@Override
	public boolean requiresFruitBlocks() {
		return false;
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime) {
		return 0xffffff;
	}

	@Override
	public boolean markAsFruitLeaf(ITreeGenome genome, World world, int x, int y, int z) {
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
	public short getIconIndex(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime, boolean fancy) {
		if (overlay != null) {
			return overlay.texUID;
		} else {
			return -1;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		if (overlay != null) {
			TextureManager.registerTexUID(register, overlay.texUID, "leaves/fruits." + overlay.ident);
		}
	}
}

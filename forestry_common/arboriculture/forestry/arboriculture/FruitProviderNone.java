/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

	private static HashMap<String, OverlayType> overlayTypes = new HashMap<String, OverlayType>();
	static {
		overlayTypes.put("berries", new OverlayType("berries", (short) 1000));
		overlayTypes.put("pomes", new OverlayType("pomes", (short) 1001));
		overlayTypes.put("nuts", new OverlayType("nuts", (short) 1002));
		overlayTypes.put("citrus", new OverlayType("citrus", (short) 1003));
		overlayTypes.put("plums", new OverlayType("plums", (short) 1004));
	}

	String key;
	IFruitFamily family = null;

	int ripeningPeriod = 10;

	OverlayType overlay = null;

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
		if(overlay != null)
			return overlay.texUID;
		else
			return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		if (overlay != null)
			TextureManager.getInstance().registerTexUID(register, overlay.texUID, "leaves/fruits." + overlay.ident);
	}
}

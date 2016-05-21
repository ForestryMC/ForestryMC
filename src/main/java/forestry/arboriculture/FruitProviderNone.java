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
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IFruitFamily;
import forestry.core.config.Constants;
import forestry.core.render.TextureManager;
import forestry.core.utils.Translator;

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

	private final String unlocalizedDescription;
	private final IFruitFamily family;

	protected int ripeningPeriod = 10;

	private OverlayType overlay = null;

	public FruitProviderNone(String unlocalizedDescription, IFruitFamily family) {
		this.unlocalizedDescription = unlocalizedDescription;
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

	@Nonnull
	@Override
	public List<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		return Collections.emptyList();
	}

	@Override
	public boolean requiresFruitBlocks() {
		return false;
	}

	@Override
	public boolean trySpawnFruitBlock(ITreeGenome genome, World world, Random rand, BlockPos pos) {
		return false;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime) {
		return 0xffffff;
	}

	@Override
	public int getDecorativeColor() {
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

	@Nonnull
	@Override
	public Map<ItemStack, Float> getProducts() {
		return Collections.emptyMap();
	}

	@Nonnull
	@Override
	public Map<ItemStack, Float> getSpecialty() {
		return Collections.emptyMap();
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal(unlocalizedDescription);
	}
	
	@Override
	public short getSpriteIndex(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime, boolean fancy) {
		if (overlay != null) {
			return overlay.texUID;
		} else {
			return -1;
		}
	}

	@Override
	public short getDecorativeSpriteIndex() {
		if (overlay != null) {
			return overlay.texUID;
		} else {
			return -1;
		}
	}

	@Override
	public void registerSprites() {
		if (overlay != null) {
			TextureManager.registerSpriteUID(overlay.texUID, "blocks/leaves/fruits." + overlay.ident);
		}
	}

	@Nullable
	@Override
	public String getModelName() {
		return null;
	}

	@Nonnull
	@Override
	public String getModID() {
		return Constants.MOD_ID;
	}
}

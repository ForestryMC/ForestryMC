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

import javax.annotation.Nullable;
import java.util.HashMap;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.products.EmptyProductList;
import forestry.api.genetics.products.IProductList;
import forestry.core.config.Constants;

import genetics.api.individual.IGenome;

public class FruitProviderNone implements IFruitProvider {

	private static class OverlayType {
		public final String ident;
		public final ResourceLocation sprite;

		public OverlayType(String ident) {
			this.ident = ident;
			this.sprite = new ResourceLocation(Constants.MOD_ID, "block/leaves/fruits." + ident);
		}
	}

	private static final HashMap<String, OverlayType> overlayTypes = new HashMap<>();

	static {
		overlayTypes.put("berries", new OverlayType("berries"));
		overlayTypes.put("pomes", new OverlayType("pomes"));
		overlayTypes.put("nuts", new OverlayType("nuts"));
		overlayTypes.put("citrus", new OverlayType("citrus"));
		overlayTypes.put("plums", new OverlayType("plums"));
	}

	private final String unlocalizedDescription;
	private final IFruitFamily family;

	protected int ripeningPeriod = 10;

	@Nullable
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

	@Override
	public NonNullList<ItemStack> getFruits(IGenome genome, Level world, BlockPos pos, int ripeningTime) {
		return NonNullList.create();
	}

	@Override
	public boolean requiresFruitBlocks() {
		return false;
	}

	@Override
	public boolean trySpawnFruitBlock(IGenome genome, LevelAccessor world, RandomSource rand, BlockPos pos) {
		return false;
	}

	@Override
	public int getColour(IGenome genome, BlockGetter world, BlockPos pos, int ripeningTime) {
		return 0xffffff;
	}

	@Override
	public int getDecorativeColor() {
		return 0xffffff;
	}

	@Override
	public boolean isFruitLeaf(IGenome genome, LevelAccessor world, BlockPos pos) {
		return false;
	}

	@Override
	public int getRipeningPeriod() {
		return ripeningPeriod;
	}

	@Override
	public IProductList getProducts() {
		return EmptyProductList.INSTANCE;
	}

	@Override
	public IProductList getSpecialty() {
		return EmptyProductList.INSTANCE;
	}

	@Override
	public MutableComponent getDescription() {
		return Component.translatable(unlocalizedDescription);
	}

	@Override
	public ResourceLocation getSprite(IGenome genome, BlockGetter world, BlockPos pos, int ripeningTime) {
		if (overlay != null) {
			return overlay.sprite;
		} else {
			return null;
		}
	}

	@Override
	public ResourceLocation getDecorativeSprite() {
		if (overlay != null) {
			return overlay.sprite;
		} else {
			return null;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		if (overlay != null) {
			event.addSprite(overlay.sprite);
		}
	}

	@Nullable
	@Override
	public String getModelName() {
		return null;
	}

	@Override
	public String getModID() {
		return Constants.MOD_ID;
	}
}

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
package forestry.arboriculture.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.items.ItemFruit;

public class AlleleFruit extends AlleleCategorized implements IAlleleFruit {

	public static IAlleleFruit fruitNone;
	public static IAlleleFruit fruitApple;
	public static IAlleleFruit fruitCocoa;
	public static IAlleleFruit fruitChestnut;
	public static IAlleleFruit fruitCoconut;
	public static IAlleleFruit fruitWalnut;
	public static IAlleleFruit fruitCherry;
	public static IAlleleFruit fruitDates;
	public static IAlleleFruit fruitPapaya;
	public static IAlleleFruit fruitLemon;
	public static IAlleleFruit fruitPlum;
	public static IAlleleFruit fruitJujube;
	private static List<IAlleleFruit> fruitAlleles;
	private static List<IAlleleFruit> fruitAllelesWithModels;

	public static void createAlleles() {
		List<IAlleleFruit> fruitAlleles = Arrays.asList(
				fruitNone = new AlleleFruit("none", new FruitProviderNone("for.fruits.none", null)),
				fruitApple = new AlleleFruit("apple", new FruitProviderRandom("for.fruits.apple", EnumFruitFamily.POMES, new ItemStack(Items.apple), 1.0f)
						.setColour(new Color(0xff2e2e))
						.setOverlay("pomes")),
				fruitCocoa = new AlleleFruit("cocoa", new FruitProviderPod("for.fruits.cocoa", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.COCOA, new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()))),
				// .setColours(0xecdca5, 0xc4d24a), true);
				fruitChestnut = new AlleleFruit("chestnut", new FruitProviderRipening("for.fruits.chestnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.CHESTNUT.getStack(), 1.0f)
						.setRipeningPeriod(6)
						.setColours(new Color(0x7f333d), new Color(0xc4d24a))
						.setOverlay("nuts"), true),
				fruitWalnut = new AlleleFruit("walnut", new FruitProviderRipening("for.fruits.walnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.WALNUT.getStack(), 1.0f)
						.setRipeningPeriod(8)
						.setColours(new Color(0xfba248), new Color(0xc4d24a))
						.setOverlay("nuts"), true),
				fruitCherry = new AlleleFruit("cherry", new FruitProviderRipening("for.fruits.cherry", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.CHERRY.getStack(), 1.0f)
						.setColours(new Color(0xff2e2e), new Color(0xc4d24a))
						.setOverlay("berries"), true),
				fruitDates = new AlleleFruit("dates", new FruitProviderPod("for.fruits.dates", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.DATES, ItemFruit.EnumFruit.DATES.getStack(4))),
				fruitPapaya = new AlleleFruit("papaya", new FruitProviderPod("for.fruits.papaya", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.PAPAYA, ItemFruit.EnumFruit.PAPAYA.getStack())),
				fruitLemon = new AlleleFruit("lemon", new FruitProviderRipening("for.fruits.lemon", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.LEMON.getStack(), 1.0f)
						.setColours(new Color(0xeeee00), new Color(0x99ff00))
						.setOverlay("citrus"), true),
				fruitPlum = new AlleleFruit("plum", new FruitProviderRipening("for.fruits.plum", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.PLUM.getStack(), 1.0f)
						.setColours(new Color(0x663446), new Color(0xeeff1a))
						.setOverlay("plums"), true)
		);

		for (IAlleleFruit fruitAllele : fruitAlleles) {
			AlleleManager.alleleRegistry.registerAllele(fruitAllele, EnumTreeChromosome.FRUITS);
		}
	}

	public static List<IAlleleFruit> getFruitAlleles() {
		if (fruitAlleles == null) {
			fruitAlleles = new ArrayList<>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleFruit) {
					IAlleleFruit alleleFruit = (IAlleleFruit) allele;
					fruitAlleles.add(alleleFruit);
				}
			}
		}
		return fruitAlleles;
	}

	public static List<IAlleleFruit> getFruitAllelesWithModels() {
		if (fruitAllelesWithModels == null) {
			fruitAllelesWithModels = new ArrayList<>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleFruit) {
					IAlleleFruit alleleFruit = (IAlleleFruit) allele;
					if (alleleFruit.getModelName() != null) {
						fruitAllelesWithModels.add(alleleFruit);
					}
				}
			}
		}
		return fruitAllelesWithModels;
	}

	@Nonnull
	private final IFruitProvider provider;

	public AlleleFruit(@Nonnull String name, @Nonnull IFruitProvider provider) {
		this(name, provider, false);
	}

	public AlleleFruit(@Nonnull String name, @Nonnull IFruitProvider provider, boolean isDominant) {
		super(Constants.MOD_ID, "fruit", name, isDominant);
		this.provider = provider;
	}

	@Nonnull
	@Override
	public IFruitProvider getProvider() {
		return this.provider;
	}
	
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

	@Nullable
	@Override
	public String getModelName() {
		return getProvider().getModelName();
	}

	@Nonnull
	@Override
	public String getModID() {
		return getProvider().getModID();
	}
	
	@Override
	public int compareTo(IAlleleFruit o) {
		return 0;
	}

}

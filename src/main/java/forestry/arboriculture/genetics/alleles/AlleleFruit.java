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

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.genetics.AlleleManager;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
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

	public static void createAlleles() {
		List<IAlleleFruit> fruitAlleles = Arrays.asList(
				fruitNone = new AlleleFruit("none", new FruitProviderNone("none", null)),
				fruitApple = new AlleleFruit("apple", new FruitProviderRandom("apple", EnumFruitFamily.POMES, new ItemStack(Items.apple), 1.0f).setColour(0xff2e2e).setOverlay("pomes")),
				fruitCocoa = new AlleleFruit("cocoa", new FruitProviderPod("cocoa", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.COCOA)),
				// .setColours(0xecdca5, 0xc4d24a), true);
				fruitChestnut = new AlleleFruit("chestnut", new FruitProviderRipening("chestnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.CHESTNUT.getStack(), 1.0f).setRipeningPeriod(6).setColours(0x7f333d, 0xc4d24a).setOverlay("nuts"), true),
				fruitWalnut = new AlleleFruit("walnut", new FruitProviderRipening("walnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.WALNUT.getStack(), 1.0f).setRipeningPeriod(8).setColours(0xfba248, 0xc4d24a).setOverlay("nuts"), true),
				fruitCherry = new AlleleFruit("cherry", new FruitProviderRipening("cherry", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.CHERRY.getStack(), 1.0f).setColours(0xff2e2e, 0xc4d24a).setOverlay("berries"), true),
				fruitDates = new AlleleFruit("dates", new FruitProviderPod("dates", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.DATES, ItemFruit.EnumFruit.DATES.getStack(4))),
				fruitPapaya = new AlleleFruit("papaya", new FruitProviderPod("papaya", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.PAPAYA, ItemFruit.EnumFruit.PAPAYA.getStack())),
				fruitLemon = new AlleleFruit("lemon", new FruitProviderRipening("lemon", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.LEMON.getStack(), 1.0f).setColours(0xeeee00, 0x99ff00).setOverlay("citrus"), true),
				fruitPlum = new AlleleFruit("plum", new FruitProviderRipening("plum", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.PLUM.getStack(), 1.0f).setColours(0x663446, 0xeeff1a).setOverlay("plums"), true)
		);

		for (IAlleleFruit fruitAllele : fruitAlleles) {
			AlleleManager.alleleRegistry.registerAllele(fruitAllele, EnumTreeChromosome.FRUITS);
		}
	}

	private final IFruitProvider provider;

	public AlleleFruit(String name, IFruitProvider provider) {
		this(name, provider, false);
	}

	public AlleleFruit(String name, IFruitProvider provider, boolean isDominant) {
		super("forestry", "fruit", name, isDominant);
		this.provider = provider;
	}

	@Override
	public IFruitProvider getProvider() {
		return this.provider;
	}
	
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

}

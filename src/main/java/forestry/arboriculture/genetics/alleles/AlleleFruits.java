package forestry.arboriculture.genetics.alleles;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
import forestry.core.items.ItemFruit;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class AlleleFruits {
	public static final IAlleleFruit fruitNone = new AlleleFruit("none", new FruitProviderNone("for.fruits.none", EnumFruitFamily.NONE));
	public static final IAlleleFruit fruitApple = new AlleleFruit("apple", new FruitProviderRandom("for.fruits.apple", EnumFruitFamily.POMES, new ItemStack(Items.APPLE), 1.0f)
			.setColour(new Color(0xff2e2e))
			.setOverlay("pomes"));
	public static final IAlleleFruit fruitCocoa = new AlleleFruit("cocoa", new FruitProviderPod("for.fruits.cocoa", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.COCOA, new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage())));
	// .setColours(0xecdca5, 0xc4d24a), true);
	public static final IAlleleFruit fruitChestnut = new AlleleFruit("chestnut", new FruitProviderRipening("for.fruits.chestnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.CHESTNUT.getStack(), 1.0f)
			.setRipeningPeriod(6)
			.setColours(new Color(0x7f333d), new Color(0xc4d24a))
			.setOverlay("nuts"), true);
	public static final IAlleleFruit fruitWalnut = new AlleleFruit("walnut", new FruitProviderRipening("for.fruits.walnut", EnumFruitFamily.NUX, ItemFruit.EnumFruit.WALNUT.getStack(), 1.0f)
			.setRipeningPeriod(8)
			.setColours(new Color(0xfba248), new Color(0xc4d24a))
			.setOverlay("nuts"), true);
	public static final IAlleleFruit fruitCherry = new AlleleFruit("cherry", new FruitProviderRipening("for.fruits.cherry", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.CHERRY.getStack(), 1.0f)
			.setColours(new Color(0xff2e2e), new Color(0xc4d24a))
			.setOverlay("berries"), true);
	public static final IAlleleFruit fruitDates = new AlleleFruit("dates", new FruitProviderPod("for.fruits.dates", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.DATES, ItemFruit.EnumFruit.DATES.getStack(4)));
	public static final IAlleleFruit fruitPapaya = new AlleleFruit("papaya", new FruitProviderPod("for.fruits.papaya", EnumFruitFamily.JUNGLE, FruitProviderPod.EnumPodType.PAPAYA, ItemFruit.EnumFruit.PAPAYA.getStack()));
	public static final IAlleleFruit fruitLemon = new AlleleFruit("lemon", new FruitProviderRipening("for.fruits.lemon", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.LEMON.getStack(), 1.0f)
			.setColours(new Color(0xeeee00), new Color(0x99ff00))
			.setOverlay("citrus"), true);
	public static final IAlleleFruit fruitPlum = new AlleleFruit("plum", new FruitProviderRipening("for.fruits.plum", EnumFruitFamily.PRUNES, ItemFruit.EnumFruit.PLUM.getStack(), 1.0f)
			.setColours(new Color(0x663446), new Color(0xeeff1a))
			.setOverlay("plums"), true);
	@Nullable
	private static List<IAlleleFruit> fruitAlleles;
	@Nullable
	private static List<IAlleleFruit> fruitAllelesWithModels;

	public static void registerAlleles() {
		for (IAlleleFruit fruitAllele : getFruitAlleles()) {
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
}

package forestry.lepidopterology.genetics.alleles;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.core.ModuleCore;
import forestry.core.items.ItemRegistryCore;

public class ButterflyAlleles {
	public static final IAlleleButterflyCocoon cocoonDefault;
	public static final IAlleleButterflyCocoon cocoonSilk;
	public static final List<IAlleleButterflyCocoon> cocoonAlleles;

	public static final IAlleleButterflyEffect butterflyNone = new AlleleButterflyEffectNone();

	static {
		cocoonAlleles = Arrays.asList(cocoonDefault = new AlleleButterflyCocoon("default", false),
			cocoonSilk = new AlleleButterflyCocoon("silk", false));
	}

	public static void registerCocoonAlleles() {
		for (IAlleleButterflyCocoon cocoonAllele : cocoonAlleles) {
			AlleleManager.alleleRegistry.registerAllele(cocoonAllele, EnumButterflyChromosome.COCOON);
		}
	}

	public static void registerEffectAlleles() {
		AlleleManager.alleleRegistry.registerAllele(butterflyNone, EnumButterflyChromosome.EFFECT);
	}

	public static void createLoot() {
		cocoonDefault.addLoot(new ItemStack(Items.STRING, 2), 1F);
		cocoonDefault.addLoot(new ItemStack(Items.STRING), 0.75F);
		cocoonDefault.addLoot(new ItemStack(Items.STRING, 3), 0.25F);

		ItemRegistryCore itemRegistry = ModuleCore.getItems();
		cocoonSilk.addLoot(new ItemStack(itemRegistry.craftingMaterial, 3, 2), 0.75F);
		cocoonSilk.addLoot(new ItemStack(itemRegistry.craftingMaterial, 2, 2), 0.25F);
	}

}

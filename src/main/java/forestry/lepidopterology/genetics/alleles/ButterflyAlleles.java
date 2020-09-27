package forestry.lepidopterology.genetics.alleles;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.genetics.IAlleleButterflyEffect;
import forestry.core.features.CoreItems;
import forestry.core.items.EnumCraftingMaterial;
import genetics.api.alleles.IAlleleRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.List;

public class ButterflyAlleles {
    public static final IAlleleButterflyCocoon cocoonDefault;
    public static final IAlleleButterflyCocoon cocoonSilk;
    public static final List<IAlleleButterflyCocoon> cocoonAlleles;

    public static final IAlleleButterflyEffect butterflyNone = new AlleleButterflyEffectNone();

    static {
        cocoonAlleles = Arrays.asList(
                cocoonDefault = new AlleleButterflyCocoon("default", false),
                cocoonSilk = new AlleleButterflyCocoon("silk", false)
        );
    }

    public static void registerAlleles(IAlleleRegistry registry) {
        for (IAlleleButterflyCocoon cocoonAllele : cocoonAlleles) {
            registry.registerAllele(cocoonAllele, ButterflyChromosomes.COCOON);
        }
        registry.registerAllele(butterflyNone, ButterflyChromosomes.EFFECT);
    }

    public static void createLoot() {
        cocoonDefault.addLoot(new ItemStack(Items.STRING, 2), 1F);
        cocoonDefault.addLoot(new ItemStack(Items.STRING), 0.75F);
        cocoonDefault.addLoot(new ItemStack(Items.STRING, 3), 0.25F);

        cocoonSilk.addLoot(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 3), 0.75F);
        cocoonSilk.addLoot(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 2), 0.25F);
    }

}

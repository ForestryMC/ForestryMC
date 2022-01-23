package forestry.lepidopterology.compat;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;
import forestry.lepidopterology.features.LepidopterologyItems;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class LepidopterologyJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		IIngredientSubtypeInterpreter<ItemStack> butterflySubtypeInterpreter = (itemStack, context) -> {
			Optional<IIndividual> individual = GeneticHelper.getIndividual(itemStack);
			return individual.map(iIndividual -> iIndividual.getGenome().getPrimary().getBinomial()).orElse(IIngredientSubtypeInterpreter.NONE);
		};

		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.BUTTERFLY_GE.item(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.COCOON_GE.item(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.CATERPILLAR_GE.item(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.SERUM_GE.item(), butterflySubtypeInterpreter);
	}
}

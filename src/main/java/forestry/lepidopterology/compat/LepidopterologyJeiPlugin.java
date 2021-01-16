package forestry.lepidopterology.compat;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;

import forestry.core.config.Constants;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
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
		if (!ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			return;
		}

		ISubtypeInterpreter butterflySubtypeInterpreter = itemStack -> {
			Optional<IIndividual> individual = GeneticHelper.getIndividual(itemStack);
			return individual.isPresent() ? individual.get().getGenome().getPrimary().getBinomial() : ISubtypeInterpreter.NONE;
		};

		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.BUTTERFLY_GE.getItem(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.COCOON_GE.getItem(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.CATERPILLAR_GE.getItem(), butterflySubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(LepidopterologyItems.SERUM_GE.getItem(), butterflySubtypeInterpreter);
	}
}

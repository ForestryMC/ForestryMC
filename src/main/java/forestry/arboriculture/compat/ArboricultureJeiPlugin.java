package forestry.arboriculture.compat;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;

import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class ArboricultureJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			return;
		}

		ISubtypeInterpreter arboSubtypeInterpreter = itemStack -> {
			Optional<IIndividual> individual = GeneticHelper.getIndividual(itemStack);
			return individual.map(iIndividual -> iIndividual.getGenome().getPrimary().getBinomial()).orElse(ISubtypeInterpreter.NONE);
		};

		subtypeRegistry.registerSubtypeInterpreter(ArboricultureItems.SAPLING.getItem(), arboSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(ArboricultureItems.POLLEN_FERTILE.getItem(), arboSubtypeInterpreter);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			return;
		}

		JeiUtil.addDescription(registration, ArboricultureItems.GRAFTER.getItem(), ArboricultureItems.GRAFTER_PROVEN.getItem());
	}
}

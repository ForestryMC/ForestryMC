package forestry.apiculture.compat;

import com.google.common.base.Preconditions;

import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.genetics.Genome;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ApicultureJeiPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			return;
		}

		ItemRegistryApiculture items = ModuleApiculture.getItems();
		Preconditions.checkNotNull(items);

		ISubtypeRegistry.ISubtypeInterpreter beeSubtypeInterpreter = itemStack -> {
			IAlleleSpecies species = Genome.getSpeciesDirectly(BeeManager.beeRoot, itemStack);
			return species == null ? ISubtypeRegistry.ISubtypeInterpreter.NONE : species.getUID();
		};

		subtypeRegistry.registerSubtypeInterpreter(items.beeDroneGE, beeSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(items.beePrincessGE, beeSubtypeInterpreter);
		subtypeRegistry.registerSubtypeInterpreter(items.beeQueenGE, beeSubtypeInterpreter);
	}

	@Override
	public void register(IModRegistry registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			return;
		}

		ItemRegistryApiculture items = ModuleApiculture.getItems();
		Preconditions.checkNotNull(items);

		JeiUtil.addDescription(registry, "frames",
			items.frameImpregnated,
			items.frameProven,
			items.frameUntreated
		);

		JeiUtil.addDescription(registry, "apiarist.suit",
			items.apiaristBoots,
			items.apiaristChest,
			items.apiaristHat,
			items.apiaristLegs
		);

		JeiUtil.addDescription(registry,
			items.habitatLocator,
			items.scoop,
			items.imprinter
		);
	}
}

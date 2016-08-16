package forestry.apiculture.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.genetics.Genome;
import forestry.core.utils.JeiUtil;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class ApicultureJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		ItemRegistryApiculture items = PluginApiculture.items;

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

		ISubtypeRegistry subtypeRegistry = registry.getJeiHelpers().getSubtypeRegistry();
		subtypeRegistry.registerNbtInterpreter(items.beeDroneGE, BeeSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(items.beePrincessGE, BeeSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(items.beeQueenGE, BeeSubtypeInterpreter.INSTANCE);
	}

	private static class BeeSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {
		public static BeeSubtypeInterpreter INSTANCE = new BeeSubtypeInterpreter();

		@Nullable
		@Override
		public String getSubtypeInfo(@Nonnull ItemStack itemStack) {
			IAlleleSpecies species = Genome.getSpeciesDirectly(BeeManager.beeRoot, itemStack);
			return species == null ? null : species.getUID();
		}
	}
}

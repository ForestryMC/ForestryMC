package forestry.apiculture.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.PluginApiculture;
import forestry.core.genetics.Genome;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ApicultureJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		List<ItemStack> frames = Arrays.asList(
				new ItemStack(PluginApiculture.items.frameImpregnated),
				new ItemStack(PluginApiculture.items.frameProven),
				new ItemStack(PluginApiculture.items.frameUntreated)
		);
		registry.addDescription(frames, "for.jei.description.frames");

		List<ItemStack> apiaristSuit = Arrays.asList(
				new ItemStack(PluginApiculture.items.apiaristBoots),
				new ItemStack(PluginApiculture.items.apiaristChest),
				new ItemStack(PluginApiculture.items.apiaristHat),
				new ItemStack(PluginApiculture.items.apiaristLegs)
		);
		registry.addDescription(apiaristSuit, "for.jei.description.apiarist.suit");

		registry.addDescription(new ItemStack(PluginApiculture.items.habitatLocator), "for.jei.description.habitat.locator");

		registry.addDescription(new ItemStack(PluginApiculture.items.scoop), "for.jei.description.scoop");

		ISubtypeRegistry subtypeRegistry = registry.getJeiHelpers().getSubtypeRegistry();
		subtypeRegistry.registerNbtInterpreter(PluginApiculture.items.beeDroneGE, BeeSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(PluginApiculture.items.beePrincessGE, BeeSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(PluginApiculture.items.beeQueenGE, BeeSubtypeInterpreter.INSTANCE);
	}

	private static class BeeSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {
		public static BeeSubtypeInterpreter INSTANCE = new BeeSubtypeInterpreter();

		@Nullable
		@Override
		public String getSubtypeInfo(@Nonnull ItemStack itemStack) {
			IAlleleSpecies species = Genome.getSpeciesDirectly(itemStack);
			return species == null ? null : species.getUID();
		}
	}
}

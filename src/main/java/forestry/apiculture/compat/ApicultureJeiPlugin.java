package forestry.apiculture.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.genetics.BeeGenome;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.INbtRegistry;
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

		INbtRegistry nbtRegistry = registry.getJeiHelpers().getNbtRegistry();
		nbtRegistry.registerNbtInterpreter(PluginApiculture.items.beeDroneGE, BeeNbtInterpreter.INSTANCE);
		nbtRegistry.registerNbtInterpreter(PluginApiculture.items.beePrincessGE, BeeNbtInterpreter.INSTANCE);
		nbtRegistry.registerNbtInterpreter(PluginApiculture.items.beeQueenGE, BeeNbtInterpreter.INSTANCE);
	}

	private static class BeeNbtInterpreter implements INbtRegistry.INbtInterpreter {
		public static BeeNbtInterpreter INSTANCE = new BeeNbtInterpreter();

		@Nullable
		@Override
		public String getSubtypeInfoFromNbt(@Nonnull NBTTagCompound nbtTagCompound) {
			IAlleleSpecies species = BeeGenome.getSpeciesDirectly(nbtTagCompound);
			return species == null ? null : species.getUID();
		}
	}
}

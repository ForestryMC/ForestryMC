package forestry.apiculture.compat;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.apiculture.PluginApiculture;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
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
	}
}

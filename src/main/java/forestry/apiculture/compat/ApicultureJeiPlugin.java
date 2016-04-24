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
	}
}

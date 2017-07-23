package forestry.plugins.compat;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.MAGICAL_AGRICULTURE, name = "mysticalagriculture", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.mysticalagriculture.description")
public class PluginMysticalAgriculture extends BlankForestryPlugin {
	private static final String MAGICAL_AGRICULTURE = "mysticalagriculture";

	public PluginMysticalAgriculture() {
	}

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(MAGICAL_AGRICULTURE);
	}

	@Override
	public String getFailMessage() {
		return "Mystical Agriculture not found";
	}

	@Nullable
	private static ItemStack getItemStack(@Nonnull String itemName) {
		ResourceLocation key = new ResourceLocation(MAGICAL_AGRICULTURE, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return new ItemStack(ForgeRegistries.ITEMS.getValue(key),1);
		} else {
			return null;
		}
	}

	@Nullable
	private static Block getBlock(@Nonnull String blockName) {
		ResourceLocation key = new ResourceLocation(MAGICAL_AGRICULTURE, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		} else {
			return null;
		}
	}

	@Override
	public void registerRecipes() {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			ImmutableList<String> crops = ImmutableList.of(
				"stone",
				"dirt",
				"nature",
				"wood",
				"water",
				"ice",
				"fire",
				"dye",
				"nether",
				"coal",
				"iron",
				"nether_quartz",
				"glowstone",
				"redstone",
				"obsidian",
				"gold",
				"lapis_lazuli",
				"end",
				"experience",
				"diamond",
				"emerald",
				"zombie",
				"pig",
				"chicken",
				"cow",
				"sheep",
				"slime",
				"skeleton",
				"creeper",
				"spider",
				"rabbit",
				"guardian",
				"blaze",
				"ghast",
				"enderman",
				"wither_skeleton",
				"rubber",
				"silicon",
				"sulfur",
				"aluminum",
				"copper",
				"saltpeter",
				"tin",
				"bronze",
				"zinc",
				"brass",
				"silver",
				"lead",
				"steel",
				"nickel",
				"constantan",
				"electrum",
				"invar",
				"mithril",
				"tungsten",
				"titanium",
				"chrome",
				"platinum",
				"iridium",
				"ruby",
				"sapphire",
				"peridot",
				"amber",
				"topaz",
				"malachite",
				"tanzanite",
				"blizz",
				"blitz",
				"basalz",
				"signalum",
				"lumium",
				"enderium",
				"aluminum_brass",
				"knightslime",
				"ardite",
				"cobalt",
				"manyullyn",
				"electrical_steel",
				"redstone_alloy",
				"conductive_iron",
				"soularium", "dark_steel",
				"pulsating_iron",
				"energetic_alloy",
				"vibrant_alloy",
				"mystical_flower",
				"manasteel",
				"terrasteel",
				"osmium",
				"glowstone_ingot",
				"refined_obsidian",
				"aquarium",
				"cold_iron",
				"star_steel",
				"adamantine",
				"marble",
				"limestone",
				"basalt",
				"apatite",
				"meteoric_iron",
				"desh",
				"vinteum",
				"chimerite",
				"blue_topaz",
				"moonstone",
				"sunstone",
				"ender_amethyst",
				"draconium",
				"yellorium",
				"certus_quartz",
				"fluix",
				"quartz_enriched_iron"
			);

			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");

			for(String cropName : crops){
				ItemStack seed = getItemStack( cropName + "_seeds");
				Block block = getBlock(cropName + "_crop");
				if (seed != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seed}, Fluids.SEED_OIL.getFluid(seedAmount));
				}
				if (seed != null && block != null) {
					Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(seed, block, BlockCrops.AGE, 7));
					Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(seed, block, BlockCrops.AGE, 7, 0));
				}
			}
		}
	}
}

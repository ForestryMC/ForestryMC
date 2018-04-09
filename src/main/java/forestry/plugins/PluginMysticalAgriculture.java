package forestry.plugins;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.MAGICAL_AGRICULTURE, name = "Mystical Agriculture", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.mysticalagriculture.description")
public class PluginMysticalAgriculture extends CompatPlugin {
	private static final String MAGICAL_AGRICULTURE = "mysticalagriculture";

	public PluginMysticalAgriculture() {
		super("Mystical Agriculture", MAGICAL_AGRICULTURE);
	}

	@Override
	public void registerRecipes() {
		if (ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
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
				"steeleaf",
				"ironwood",
				"knightmetal",
				"fiery_ingot",
				"meteoric_iron",
				"desh",
				"black_quartz",
				"vinteum",
				"chimerite",
				"blue_topaz",
				"moonstone",
				"sunstone",
				"aquamarine",
				"starmetal",
				"rock_crystal",
				"ender_biotite",
				"slate",
				"ender_amethyst",
				"draconium",
				"yellorium",
				"certus_quartz",
				"fluix",
				"quartz_enriched_iron"
			);

			IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");

			for (String cropName : crops) {
				ItemStack seeds = getItemStack(cropName + "_seeds");
				Block block = getBlock(cropName + "_crop");
				if (seeds != null) {
					RecipeManagers.squeezerManager.addRecipe(10, seeds, Fluids.SEED_OIL.getFluid(seedAmount));
				}
				if (seeds != null && block != null) {
					farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(seeds, block, BlockCrops.AGE, 7, 0));
				}
			}
			for(int i = 1;i <= 5;i++){
				ItemStack seeds = getItemStack("tier" + i + "_inferium_seeds");
				Block block = getBlock("tier" + i + "_inferium_crop");
				if (seeds != null) {
					RecipeManagers.squeezerManager.addRecipe(10, seeds, Fluids.SEED_OIL.getFluid(seedAmount));
				}
				if (seeds != null && block != null) {
					farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(seeds, block, BlockCrops.AGE, 7, 0));
				}
			}
		}
	}
}

package forestry.plugins;

import com.google.common.collect.ImmutableMap;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.circuits.Circuits;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.FarmRegistry;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicRubber;
import forestry.farming.logic.farmables.FarmableSapling;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.TECH_REBORN, name = "TechReborn", author = "temp1011", url = Constants.URL, unlocalizedDescription = "for.module.techreborn.description")
public class PluginTechReborn extends CompatPlugin {

	public static final String MOD_ID = "techreborn";

	@ItemStackHolder("techreborn:rubber_log")
	public static ItemStack RUBBER_WOOD = null;
	@ItemStackHolder("techreborn:rubber_sapling")
	public static final ItemStack RUBBER_SAPLING = null;
	@ItemStackHolder(value = "techreborn:part", meta = 31)
	public static ItemStack SAP = null;
	@ItemStackHolder(value = "techreborn:part", meta = 32)
	public static final ItemStack RUBBER = null;

	public PluginTechReborn() {
		super("TechReborn", MOD_ID);
	}

	@Override
	public void preInit() {
		IFarmProperties rubberFarm = FarmRegistry.getInstance().registerLogic("farmRubber", FarmLogicRubber::new);

		Circuits.farmRubberManual = new CircuitFarmLogic("manualRubber", rubberFarm, true);
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		ItemStack scrap = getItemStack("part", 33);
		ItemStack uuMatter = getItemStack("uumatter");
		ModuleHelper.registerCrate(SAP);
		ModuleHelper.registerCrate(scrap);
		ModuleHelper.registerCrate(uuMatter);
		ModuleHelper.registerCrate("ingotLead");
		ModuleHelper.registerCrate("ingotAluminum");
		ModuleHelper.registerCrate("ingotBrass");
		ModuleHelper.registerCrate("ingotNickel");

		if (!ModUtil.isModLoaded(PluginIC2.MOD_ID)) {
			ModuleHelper.registerCrate("ingotSilver");
			ModuleHelper.registerCrate("itemRubber");
		}
	}

	@Override
	public void registerBackpackItems() {
		ModuleHelper.addItemToBackpack(BackpackManager.FORESTER_UID, SAP);
	}

	@Override
	public void registerRecipes() {
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY,
				Fluids.GLASS.getFluid(500),
				ModuleCore.items.tubes.get(EnumElectronTube.RUBBER, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "itemRubber"});

		ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
		if (!ModUtil.isModLoaded(PluginIC2.MOD_ID)) {
			RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(SAP, 1.0f));
		} else {
			Log.info("Using ic2 Propolis recipe rather than Tech Reborn");
		}

		int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
		if (bogEarthOutputCan > 0) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
			waterBucket.writeToNBT(fluidTag);
			ItemStack waterCell = getItemStack("dynamiccell");
			waterCell.setTagCompound(fluidTag);
			ItemStack bogEarthCan = ModuleCore.getBlocks().bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
			RecipeUtil.addRecipe("techreborn_bog_earth_can", bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waterCell, 'Y', "sand");
		}

		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING))) {
			ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
			ChipsetManager.solderManager.addRecipe(layoutManual, ModuleCore.items.tubes.get(EnumElectronTube.RUBBER, 1), Circuits.farmRubberManual);
			ForestryAPI.farmRegistry.registerFarmables("rubberTreeFarm", new FarmableSapling(RUBBER_SAPLING, new ItemStack[0]));
		}
	}

	public static boolean rubberItemsSuccess() {
		return ItemStackUtil.getItemFromRegistry("techreborn:rubber_wood") != null
				&& new ItemStack(ItemStackUtil.getItemFromRegistry("techreborn:part"), 1, 31).isEmpty();
	}
}
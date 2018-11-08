package forestry.storage;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.StorageManager;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.items.ItemCrated;
import forestry.storage.items.ItemRegistryCrates;
import forestry.storage.proxy.ProxyCrates;

@ForestryModule(moduleID = ForestryModuleUids.CRATE, containerID = Constants.MOD_ID, name = "Crate", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.crates.description")
public class ModuleCrates extends BlankForestryModule {

	private static final String CONFIG_CATEGORY = "crates";

	public static final List<String> cratesRejectedOreDict = new ArrayList<>();
	public static Multimap<Item, ItemStack> cratesRejectedItem = HashMultimap.create();

	private static final List<ItemCrated> crates = new ArrayList<>();

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.storage.proxy.ProxyCratesClient", serverSide = "forestry.storage.proxy.ProxyCrates")
	public static ProxyCrates proxy;

	@Nullable
	private static ItemRegistryCrates items;

	public static ItemRegistryCrates getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public void setupAPI() {
		StorageManager.crateRegistry = new CrateRegistry();
		proxy.registerCrateModel();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryCrates();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
		if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "1.0.0");
			}
		}

		handleConfig(config);

		config.save();
	}

	private void handleConfig(LocalizedConfiguration config) {

		// accepted items
		{
			String[] crateItemList = config.getStringListLocalized("crates.items", "accepted", Constants.EMPTY_STRINGS);
			List<ItemStack> crateItems = ItemStackUtil.parseItemStackStrings(crateItemList, OreDictionary.WILDCARD_VALUE);
			for (ItemStack crateItem : crateItems) {
				StorageManager.crateRegistry.registerCrate(crateItem);
			}
		}

		// rejected items
		{
			String[] crateItemList = config.getStringListLocalized("crates.items", "rejected", Constants.EMPTY_STRINGS);
			for (ItemStack stack : ItemStackUtil.parseItemStackStrings(crateItemList, OreDictionary.WILDCARD_VALUE)) {
				cratesRejectedItem.put(stack.getItem(), stack);
			}
		}

		// accepted oreDict
		{
			String[] crateOreDictList = config.getStringListLocalized("crates.oredict", "accepted", Constants.EMPTY_STRINGS);

			for (String name : OreDictionary.getOreNames()) {
				if (name == null) {
					Log.error("Found a null oreName in the ore dictionary");
				} else {
					for (String regex : crateOreDictList) {
						if (name.matches(regex)) {
							StorageManager.crateRegistry.registerCrate(name);
						}
					}
				}
			}
		}

		// rejected oreDict
		{
			String[] crateOreDictList = config.getStringListLocalized("crates.oredict", "rejected", Constants.EMPTY_STRINGS);

			for (String name : OreDictionary.getOreNames()) {
				if (name == null) {
					Log.error("Found a null oreName in the ore dictionary");
				} else {
					for (String regex : crateOreDictList) {
						if (name.matches(regex)) {
							cratesRejectedOreDict.add(name);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean processIMCMessage(FMLInterModComms.IMCMessage message) {
		if (message.key.equals("add-crate-items")) {
			ItemStack value = message.getItemStackValue();
			if (value != null) {
				StorageManager.crateRegistry.registerCrate(value);
			} else {
				IMCUtil.logInvalidIMCMessage(message);
			}
			return true;
		} else if (message.key.equals("add-crate-oredict")) {
			String value = message.getStringValue();
			StorageManager.crateRegistry.registerCrate(value);
			return true;
		} else if (message.key.equals("blacklist-crate-item")) {
			ItemStack value = message.getItemStackValue();
			if (value != null) {
				cratesRejectedItem.put(value.getItem(), value);
			} else {
				IMCUtil.logInvalidIMCMessage(message);
			}
			return true;
		} else if (message.key.equals("blacklist-crate-oredict")) {
			cratesRejectedOreDict.add(message.getStringValue());
			return true;
		}
		return false;
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE));
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCrates items = getItems();

		// / CARPENTER
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// / CRATES
			RecipeManagers.carpenterManager.addRecipe(20, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), ItemStack.EMPTY, items.crate.getItemStack(24),
				" # ", "# #", " # ", '#', "logWood");
		}
	}

	public static void registerCrate(ItemCrated crate) {
		crates.add(crate);
	}

	public static void createCrateRecipes() {
		for (ItemCrated crate : crates) {
			ItemStack crateStack = new ItemStack(crate);
			ItemStack uncrated = crate.getContained();
			if (!uncrated.isEmpty()) {
				if (crate.getOreDictName() != null) {
					addCrating(crateStack, crate.getOreDictName());
				} else {
					addCrating(crateStack, uncrated);
				}
				addUncrating(crateStack, uncrated);
			}
		}
	}


	private static void addCrating(ItemStack crateStack, Object uncrated) {
		FluidStack water = new FluidStack(FluidRegistry.WATER, Constants.CARPENTER_CRATING_LIQUID_QUANTITY);
		ItemStack box = getItems().crate.getItemStack();
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_CRATING_CYCLES, water, box, crateStack, "###", "###", "###", '#', uncrated);
	}

	private static void addUncrating(ItemStack crateStack, ItemStack uncrated) {
		ItemStack product = new ItemStack(uncrated.getItem(), 9, uncrated.getItemDamage());
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_UNCRATING_CYCLES, ItemStack.EMPTY, product, "#", '#', crateStack);
	}
}

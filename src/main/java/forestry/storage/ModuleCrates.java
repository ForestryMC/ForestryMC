package forestry.storage;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.StorageManager;
import forestry.core.config.Constants;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.items.ItemCrated;
import forestry.storage.items.ItemRegistryCrates;
import forestry.storage.models.ModelCrate;
import forestry.storage.proxy.ProxyStorage;

@ForestryModule(moduleID = ForestryModuleUids.CRATE, containerID = Constants.MOD_ID, name = "Crate", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.crates.description")
public class ModuleCrates extends BlankForestryModule {

	private static final List<ItemCrated> crates = new ArrayList<>();

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.storage.proxy.ProxyStorageClient", serverSide = "forestry.storage.proxy.ProxyStorage")
	public static ProxyStorage proxy;

	@Nullable
	private static ItemRegistryCrates items;

	public static ItemRegistryCrates getItems() {
		Preconditions.checkState(items != null);
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBakeModel(ModelBakeEvent event) {
		ModelCrate.onModelBake(event);
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

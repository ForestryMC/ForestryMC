package forestry.apiculture.modules;

import de.nedelosk.modularmachines.api.material.EnumVanillaMaterials;
import de.nedelosk.modularmachines.api.modules.EnumModuleSizes;
import de.nedelosk.modularmachines.api.modules.IModule;
import de.nedelosk.modularmachines.api.modules.containers.ModuleContainer;
import de.nedelosk.modularmachines.api.modules.containers.ModuleItemContainer;
import de.nedelosk.modularmachines.api.modules.position.EnumModulePositions;
import de.nedelosk.modularmachines.api.modules.storage.module.StorageModuleProperties;
import forestry.apiculture.PluginApiculture;
import forestry.core.config.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModuleRegistryApiculture {
	
	public static IModule moduleBeeHouse;
	public static IModule moduleApiary;
	
	public static void registerModules(){
		moduleBeeHouse = new ModuleBeeHouse("bee_house", false);
		moduleBeeHouse.setRegistryName(new ResourceLocation(Constants.MOD_ID, "bee_house"));
		GameRegistry.register(moduleBeeHouse);
		
		moduleApiary = new ModuleBeeHouse("apiary", true);
		moduleApiary.setRegistryName(new ResourceLocation(Constants.MOD_ID, "apiary"));
		GameRegistry.register(moduleApiary);
	}
	
	public static void registerModuleContainers(){
		GameRegistry.register(new ModuleItemContainer(new ItemStack(PluginApiculture.blocks.beeHouse), EnumVanillaMaterials.WOOD, EnumModuleSizes.LARGE, new ModuleContainer(moduleBeeHouse, new StorageModuleProperties(1, EnumModulePositions.SIDE))));
		GameRegistry.register(new ModuleItemContainer(new ItemStack(PluginApiculture.blocks.apiary), EnumVanillaMaterials.WOOD, EnumModuleSizes.LARGE, new ModuleContainer(moduleApiary, new StorageModuleProperties(2, EnumModulePositions.SIDE))));
	}
	
}

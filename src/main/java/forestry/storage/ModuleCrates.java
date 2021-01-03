package forestry.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.storage.StorageManager;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.IMCUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.modules.features.FeatureItem;
import forestry.storage.items.ItemCrated;
import forestry.storage.proxy.ProxyCrates;
import forestry.storage.proxy.ProxyCratesClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ForestryModule(moduleID = ForestryModuleUids.CRATE, containerID = Constants.MOD_ID, name = "Crate", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.crates.description")
public class ModuleCrates extends BlankForestryModule {

    private static final String CONFIG_CATEGORY = "crates";

    public static final List<String> cratesRejectedOreDict = new ArrayList<>();
    public static final Multimap<Item, ItemStack> cratesRejectedItem = HashMultimap.create();

    public static final List<FeatureItem<ItemCrated>> crates = new ArrayList<>();

    @Nullable
    public static ProxyCrates proxy;

    public ModuleCrates() {
        proxy = DistExecutor.runForDist(() -> ProxyCratesClient::new, () -> ProxyCrates::new);
    }

    @Override
    public void setupAPI() {
        StorageManager.crateRegistry = new CrateRegistry();
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
        //		{
        //			String[] crateItemList = config.getStringListLocalized("crates.items", "accepted", Constants.EMPTY_STRINGS);
        //			List<ItemStack> crateItems = ItemStackUtil.parseItemStackStrings(crateItemList, OreDictionary.WILDCARD_VALUE);
        //			for (ItemStack crateItem : crateItems) {
        //				StorageManager.crateRegistry.registerCrate(crateItem);
        //			}
        //		}
        //
        //		// rejected items
        //		{
        //			String[] crateItemList = config.getStringListLocalized("crates.items", "rejected", Constants.EMPTY_STRINGS);
        //			for (ItemStack stack : ItemStackUtil.parseItemStackStrings(crateItemList, OreDictionary.WILDCARD_VALUE)) {
        //				cratesRejectedItem.put(stack.getItem(), stack);
        //			}
        //		}
        //
        //		// accepted oreDict
        //		{
        //			String[] crateOreDictList = config.getStringListLocalized("crates.oredict", "accepted", Constants.EMPTY_STRINGS);
        //
        //			for (String name : OreDictionary.getOreNames()) {
        //				if (name == null) {
        //					Log.error("Found a null oreName in the ore dictionary");
        //				} else {
        //					for (String regex : crateOreDictList) {
        //						if (name.matches(regex)) {
        //							StorageManager.crateRegistry.registerCrate(name);
        //						}
        //					}
        //				}
        //			}
        //		}
        //
        //		// rejected oreDict
        //		{
        //			String[] crateOreDictList = config.getStringListLocalized("crates.oredict", "rejected", Constants.EMPTY_STRINGS);
        //
        //			for (String name : OreDictionary.getOreNames()) {
        //				if (name == null) {
        //					Log.error("Found a null oreName in the ore dictionary");
        //				} else {
        //					for (String regex : crateOreDictList) {
        //						if (name.matches(regex)) {
        //							cratesRejectedOreDict.add(name);
        //						}
        //					}
        //				}
        //			}
        //		}	//TODO new config, tags
    }

    //TODO new imc
    @Override
    public boolean processIMCMessage(InterModComms.IMCMessage message) {
//        switch (message.getMethod()) {
//            case "add-crate-items": {
//                ItemStack value = (ItemStack) message.getMessageSupplier().get();
//                if (value != null) {
//                    StorageManager.crateRegistry.registerCrate(value);
//                } else {
//                    IMCUtil.logInvalidIMCMessage(message);
//                }
//                return true;
//            }
//            case "add-crate-oredict": {
//                String value = (String) message.getMessageSupplier().get();
//                StorageManager.crateRegistry.registerCrate(value);
//                return true;
//            }
//            case "blacklist-crate-item": {
//                ItemStack value = (ItemStack) message.getMessageSupplier().get();
//                if (value != null) {
//                    cratesRejectedItem.put(value.getItem(), value);
//                } else {
//                    IMCUtil.logInvalidIMCMessage(message);
//                }
//                return true;
//            }
//            case "blacklist-crate-oredict":
//                cratesRejectedOreDict.add((String) message.getMessageSupplier().get());
//                return true;
//        }
        return false;
    }

    public static void registerCrate(FeatureItem<ItemCrated> crate) {
        crates.add(crate);
    }

    @Override
    public ISidedModuleHandler getModuleHandler() {
        return proxy;
    }
}

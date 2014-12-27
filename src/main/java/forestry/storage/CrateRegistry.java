package forestry.storage;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.storage.ICrateRegistry;
import forestry.core.items.ItemCrated;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;
import forestry.plugins.PluginStorage;

public class CrateRegistry implements ICrateRegistry {

	private void registerCrate(ItemStack stack, String uid, boolean useOreDict) {
		if (!EnumSet.of(PluginManager.Stage.INIT).contains(PluginManager.getStage()))
			throw new RuntimeException("Tried to make a crate outside of Init");

		if (stack == null || stack.getItem() == null)
			throw new RuntimeException("Tried to make a crate without an item");

		if (uid == null)
			throw new RuntimeException("Tried to make a crate without a uid");

		ItemCrated crate = new ItemCrated(stack, useOreDict);
		crate.setUnlocalizedName(uid);
		Proxies.common.registerItem(crate);
		PluginStorage.registerCrate(crate);
	}

	@Override
	public void registerCrate(Item item, String uid) {
		registerCrate(new ItemStack(item), uid, false);
	}

	@Override
	public void registerCrateUsingOreDict(Item item, String uid) {
		registerCrate(new ItemStack(item), uid, true);
	}

	@Override
	public void registerCrate(Block block, String uid) {
		registerCrate(new ItemStack(block), uid, false);
	}

	@Override
	public void registerCrateUsingOreDict(Block block, String uid) {
		registerCrate(new ItemStack(block), uid, true);
	}

	@Override
	public void registerCrate(ItemStack stack, String uid) {
		registerCrate(stack, uid, false);
	}

	@Override
	public void registerCrateUsingOreDict(ItemStack stack, String uid) {
		registerCrate(stack, uid, true);
	}
}

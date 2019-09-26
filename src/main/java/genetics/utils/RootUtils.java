package genetics.utils;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticsAPI;

public class RootUtils {

	private RootUtils() {
	}

	public static boolean hasRoot(ItemStack stack) {
		return GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(stack).isPresent();
	}
}

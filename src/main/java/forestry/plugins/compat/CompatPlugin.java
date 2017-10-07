package forestry.plugins.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;

public abstract class CompatPlugin extends BlankForestryPlugin {

	protected final String modName;
	protected final String modID;

	public CompatPlugin(String modName, String modID) {
		this.modName = modName;
		this.modID = modID;
	}

	@Override
	public final boolean isAvailable() {
		return ModUtil.isModLoaded(modID);
	}

	@Override
	public final String getFailMessage() {
		return modName + " not found";
	}

	@Nullable
	protected ItemStack getItemStack(@Nonnull String itemName) {
		return getItemStack(itemName, 0);
	}

	@Nullable
	protected ItemStack getItemStack(@Nonnull String itemName, int meta) {
		Item item = getItem(itemName);
		if (item == null) {
			return null;
		}
		return new ItemStack(item, 1, meta);
	}

	@Nullable
	protected Block getBlock(@Nonnull String blockName) {
		ResourceLocation key = new ResourceLocation(modID, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		}
		Log.debug("Could not find block {}", key);
		return null;
	}

	@Nullable
	protected Item getItem(String itemName) {
		ResourceLocation key = new ResourceLocation(modID, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return ForgeRegistries.ITEMS.getValue(key);
		}
		Log.debug("Could not find item {}", key);
		return null;
	}

	@Nullable
	protected Fluid getFluid(String fluidName) {
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid == null) {
			Log.debug("Could not find fluid {}", fluidName);
		}
		return fluid;
	}
}

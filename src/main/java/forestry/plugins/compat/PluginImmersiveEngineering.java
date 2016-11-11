package forestry.plugins.compat;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.CropDestroy;
import forestry.farming.logic.FarmableBase;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ForestryPlugin(pluginID = ForestryPluginUids.IMMERSIVE_ENGINEERING, name = "ImmersiveEngineering", author = "marcin212", url = Constants.URL, unlocalizedDescription = "for.plugin.immersiveengineering.description")
public class PluginImmersiveEngineering extends BlankForestryPlugin {
	private static final String IE = "immersiveengineering";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(IE);
	}

	@Override
	public String getFailMessage() {
		return "ImmersiveEngineering not found";
	}

	@Override
	public void postInit() {
		ItemStack hempSeed = getItemStack("seed");
		Block hempCrop = getBlock("hemp");
		int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		IProperty age = hempCrop.getBlockState().getProperties().stream().filter(p -> p.getName().equals("type")).findAny().get();

		if (hempSeed != null && hempCrop != null && age != null) {
			IBlockState planted = hempCrop.getDefaultState().withProperty(age, (Comparable) age.parseValue("bottom0").get());
			IBlockState mature = hempCrop.getDefaultState().withProperty(age, (Comparable) age.parseValue("top0").get());
			Farmables.farmables.get("farmWheat").add(new FarmableBase(hempSeed, planted, mature, false) {
				@Override
				public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
					IBlockState stateUp = world.getBlockState(pos.up());
					if (stateUp != matureState) {
						return null;
					}
					return new CropDestroy(world, stateUp, pos.up(), null);
				}
			});

			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hempSeed}, Fluids.SEED_OIL.getFluid(seedAmount));
		}
	}

	private static Block getBlock(@Nonnull String blockName) {
		ResourceLocation key = new ResourceLocation(IE, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		} else {
			return null;
		}
	}

	@Nullable
	private static ItemStack getItemStack(@Nonnull String itemName, int meta) {
		ResourceLocation key = new ResourceLocation(IE, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return new ItemStack(ForgeRegistries.ITEMS.getValue(key), 1, meta);
		} else {
			return null;
		}
	}

	@Nullable
	private static ItemStack getItemStack(@Nonnull String itemName) {
		return getItemStack(itemName, 0);
	}
}

package forestry.arboriculture;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodItemMeshDefinition;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.core.config.Constants;
import forestry.core.utils.Translator;

public class WoodHelper {

	public static String getDisplayName(IWoodTyped wood, IWoodType woodType) {
		WoodBlockKind blockKind = wood.getBlockKind();

		String displayName;

		if (woodType instanceof EnumForestryWoodType) {
			String customUnlocalizedName = "tile.for." + blockKind + "." + woodType + ".name";
			if (Translator.canTranslateToLocal(customUnlocalizedName)) {
				displayName = Translator.translateToLocal(customUnlocalizedName);
			} else {
				String woodGrammar = Translator.translateToLocal("for." + blockKind + ".grammar");
				String woodTypeName = Translator.translateToLocal("for.trees.woodType." + woodType);

				displayName = woodGrammar.replaceAll("%TYPE", woodTypeName);
			}
		} else if (woodType instanceof EnumVanillaWoodType) {
			displayName = TreeManager.woodAccess.getStack(woodType, blockKind, false).getDisplayName();
		} else {
			throw new IllegalArgumentException("Unknown wood type: " + woodType);
		}

		if (wood.isFireproof()) {
			displayName = Translator.translateToLocalFormatted("tile.for.fireproof", displayName);
		}

		return displayName;
	}

	public static ResourceLocation getDefaultResourceLocations(IWoodTyped typed) {
		return new ResourceLocation(Constants.MOD_ID, typed.getBlockKind().toString());
	}

	public static ResourceLocation[] getResourceLocations(IWoodTyped typed) {
		List<ResourceLocation> resourceLocations = new ArrayList<>();
		WoodBlockKind blockKind = typed.getBlockKind();
		for (IWoodType woodType : typed.getWoodTypes()) {
			if (woodType instanceof EnumVanillaWoodType) {
				resourceLocations.add(new ResourceLocation("minecraft", woodType + "_" + blockKind));
			} else {
				resourceLocations.add(new ResourceLocation(Constants.MOD_ID, blockKind + "/" + woodType));
			}
		}
		return resourceLocations.toArray(new ResourceLocation[resourceLocations.size()]);
	}

	@SideOnly(Side.CLIENT)
	public static class WoodMeshDefinition implements IWoodItemMeshDefinition {

		public final IWoodTyped wood;

		public WoodMeshDefinition(IWoodTyped wood) {
			this.wood = wood;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			int meta = stack.getMetadata();
			IWoodType woodType = wood.getWoodType(meta);
			WoodBlockKind blockKind = wood.getBlockKind();
			if (woodType instanceof EnumVanillaWoodType) {
				return new ModelResourceLocation("minecraft:" + woodType + "_" + blockKind, "inventory");
			} else {
				String modID = Constants.MOD_ID;
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != null) {
					modID = block.getRegistryName().getNamespace();
				}
				return new ModelResourceLocation(modID + ":" + blockKind + "/" + woodType, "inventory");
			}
		}

		@Override
		public ResourceLocation getDefaultModelLocation(ItemStack stack) {
			WoodBlockKind blockKind = wood.getBlockKind();
			return new ResourceLocation("forestry:item/" + blockKind.toString());
		}

	}
}

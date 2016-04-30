package forestry.arboriculture;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.core.models.ModelManager;
import forestry.core.render.ForestryResource;
import forestry.core.utils.Translator;

public class WoodHelper {
	@Nonnull
	public static String getDisplayName(IWoodTyped wood, EnumWoodType woodType) {
		String blockKind = wood.getBlockKind();

		String displayName;
		String customUnlocalizedName = "tile.for." + blockKind + "." + woodType.ordinal() + ".name";
		if (Translator.canTranslateToLocal(customUnlocalizedName)) {
			displayName = Translator.translateToLocal(customUnlocalizedName);
		} else {
			String woodGrammar = Translator.translateToLocal("for." + blockKind + ".grammar");
			String woodTypeName = Translator.translateToLocal("for.trees.woodType." + woodType);

			displayName = woodGrammar.replaceAll("%TYPE", woodTypeName);
		}

		if (wood.isFireproof()) {
			displayName = Translator.translateToLocalFormatted("tile.for.fireproof", displayName);
		}

		return displayName;
	}

	public static ResourceLocation[] getResourceLocations(IWoodTyped typed) {
		List<ResourceLocation> resourceLocations = new ArrayList<>();
		String blockKind = typed.getBlockKind();
		for (EnumWoodType woodType : typed.getWoodTypes()) {
			resourceLocations.add(new ForestryResource(blockKind + "/" + woodType));
		}
		return resourceLocations.toArray(new ResourceLocation[resourceLocations.size()]);
	}

	@SideOnly(Side.CLIENT)
	public static class WoodMeshDefinition implements ItemMeshDefinition {
		@Nonnull
		public IWoodTyped wood;

		public WoodMeshDefinition(@Nonnull IWoodTyped wood) {
			this.wood = wood;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			int meta = stack.getMetadata();
			EnumWoodType woodType = wood.getWoodType(meta);
			String blockKind = wood.getBlockKind();
			return ModelManager.getInstance().getModelLocation(blockKind + "/" + woodType);
		}

	}
}

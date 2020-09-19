package forestry.arboriculture;

import forestry.api.arboriculture.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class WoodHelper {

    public static ITextComponent getDisplayName(IWoodTyped wood, IWoodType woodType) {
        WoodBlockKind blockKind = wood.getBlockKind();

        ITextComponent displayName;

        if (woodType instanceof EnumForestryWoodType) {
            String customUnlocalizedName = "block.forestry." + blockKind + "." + woodType;
            if (I18n.hasKey(customUnlocalizedName)) {
                displayName = new TranslationTextComponent(customUnlocalizedName);
            } else {
                displayName = new TranslationTextComponent(
                        "for." + blockKind + ".grammar",
                        new TranslationTextComponent("for.trees.woodType." + woodType)
                );
            }
        } else if (woodType instanceof EnumVanillaWoodType) {
            displayName = TreeManager.woodAccess.getStack(woodType, blockKind, false).getDisplayName();
        } else {
            throw new IllegalArgumentException("Unknown wood type: " + woodType);
        }

        if (wood.isFireproof()) {
            displayName = new TranslationTextComponent("block.forestry.fireproof", displayName);
        }

        return displayName;
    }
}

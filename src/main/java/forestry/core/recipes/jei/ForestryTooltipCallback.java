package forestry.core.recipes.jei;

import com.google.common.collect.ArrayListMultimap;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.NumberFormat;
import java.util.List;

public class ForestryTooltipCallback implements ITooltipCallback<ItemStack> {
    private final ArrayListMultimap<Integer, ITextComponent> tooltips = ArrayListMultimap.create();

    @Override
    public void onTooltip(int index, boolean input, ItemStack ingredient, List<ITextComponent> tooltip) {
        List<ITextComponent> tip = tooltips.get(index);
        if (!tip.isEmpty()) {
            tooltip.addAll(tip);
        }
    }

    public void addFortuneTooltip(int index) {
        tooltips.get(index).add(new TranslationTextComponent("for.jei.fortune").mergeStyle(TextFormatting.GRAY));
    }

    public void addChanceTooltip(int index, float chance) {
        if (chance < 0) {
            chance = 0;
        } else if (chance > 1.0) {
            chance = 1.0f;
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);
        String chanceString = String.valueOf(percentFormat.format(chance));

        tooltips.get(index).add(new TranslationTextComponent("for.jei.chance", chanceString).mergeStyle(TextFormatting.GRAY));
    }
}

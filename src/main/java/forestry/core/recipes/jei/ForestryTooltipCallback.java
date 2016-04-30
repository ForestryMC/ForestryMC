package forestry.core.recipes.jei;

import com.google.common.collect.ArrayListMultimap;

import java.text.NumberFormat;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import forestry.core.utils.Translator;

import mezz.jei.api.gui.ITooltipCallback;

public class ForestryTooltipCallback implements ITooltipCallback<ItemStack> {

	private final ArrayListMultimap<Integer, String> tooltips = ArrayListMultimap.create();
	
	public void addToTooltip(int index, List<String> tooltip) {
		tooltips.get(index).addAll(tooltip);
	}
	
	public void addToTooltip(int index, String tooltip) {
		tooltips.get(index).add(tooltip);
	}
	
	public List<String> getTooltip(int index) {
		return tooltips.get(index);
	}
	
	@Override
	public void onTooltip(int index, boolean input, ItemStack ingredient, List<String> tooltip) {
		List<String> tip = tooltips.get(index);
		if (!tip.isEmpty()) {
			tooltip.addAll(tip);
		}
	}
	
	public void addChanceTooltip(int index, float chance) {
		if (chance <= 0.0F) {
			tooltips.get(index).add(EnumChatFormatting.GRAY + String.format(Translator.translateToLocal("forestry.jei.chance"), Translator.translateToLocal("forestry.jei.chance.never")));
		} else if (chance < 0.01F) {
			tooltips.get(index).add(EnumChatFormatting.GRAY + String.format(Translator.translateToLocal("forestry.jei.chance"), Translator.translateToLocal("forestry.jei.chance.lessThan1")));
		} else if (chance != 1.0F) {
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMaximumFractionDigits(2);
			tooltips.get(index).add(EnumChatFormatting.GRAY + String.format(Translator.translateToLocal("forestry.jei.chance"), String.valueOf(percentFormat.format(chance))));
		}
	}

}

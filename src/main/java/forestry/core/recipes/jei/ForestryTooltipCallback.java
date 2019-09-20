//package forestry.core.recipes.jei;
//
//import com.google.common.collect.ArrayListMultimap;
//
//import java.text.NumberFormat;
//import java.util.List;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.text.TextFormatting;
//
//import forestry.core.utils.Translator;
//
//import mezz.jei.api.gui.ITooltipCallback;
//
//public class ForestryTooltipCallback implements ITooltipCallback<ItemStack> {
//
//	private final ArrayListMultimap<Integer, String> tooltips = ArrayListMultimap.create();
//
//	@Override
//	public void onTooltip(int index, boolean input, ItemStack ingredient, List<String> tooltip) {
//		List<String> tip = tooltips.get(index);
//		if (!tip.isEmpty()) {
//			tooltip.addAll(tip);
//		}
//	}
//
//	public void addFortuneTooltip(int index) {
//		tooltips.get(index).add(TextFormatting.GRAY + Translator.translateToLocalFormatted("for.jei.fortune"));
//	}
//
//	public void addChanceTooltip(int index, float chance) {
//		if (chance < 0) {
//			chance = 0;
//		} else if (chance > 1.0) {
//			chance = 1.0f;
//		}
//
//		NumberFormat percentFormat = NumberFormat.getPercentInstance();
//		percentFormat.setMaximumFractionDigits(2);
//		String chanceString = String.valueOf(percentFormat.format(chance));
//
//		tooltips.get(index).add(TextFormatting.GRAY + Translator.translateToLocalFormatted("for.jei.chance", chanceString));
//	}
//
//}

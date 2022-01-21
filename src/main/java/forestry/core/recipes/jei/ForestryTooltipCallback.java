package forestry.core.recipes.jei;

import com.google.common.collect.ArrayListMultimap;

import java.text.NumberFormat;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

import mezz.jei.api.gui.ingredient.ITooltipCallback;

public class ForestryTooltipCallback implements ITooltipCallback<ItemStack> {
	private final ArrayListMultimap<Integer, Component> tooltips = ArrayListMultimap.create();

	@Override
	public void onTooltip(int index, boolean input, ItemStack ingredient, List<Component> tooltip) {
		List<Component> tip = tooltips.get(index);
		if (!tip.isEmpty()) {
			tooltip.addAll(tip);
		}
	}

	public void addFortuneTooltip(int index) {
		tooltips.get(index).add(new TranslatableComponent("for.jei.fortune").withStyle(ChatFormatting.GRAY));
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

		tooltips.get(index).add(new TranslatableComponent("for.jei.chance", chanceString).withStyle(ChatFormatting.GRAY));
	}
}

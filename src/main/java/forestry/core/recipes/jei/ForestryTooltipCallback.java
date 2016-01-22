package forestry.core.recipes.jei;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.ITooltipCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ForestryTooltipCallback implements ITooltipCallback<ItemStack>{

	private List<String>[] tip;
	
	public ForestryTooltipCallback(int slots) {
		tip = new List[slots];
		for(int i = 0;i < slots;i++){
			tip[i] = new ArrayList<String>();
		}
	}
	
	public void addToTooltip(int index, List<String> tooltip) {
		this.tip[index].addAll(tooltip);
	}
	
	public void addToTooltip(int index, String tooltip) {
		this.tip[index].add(tooltip);
	}
	
	public List<String> getTooltip(int index) {
		return tip[index];
	}
	
	public List<String>[] getTooltip() {
		return tip;
	}
	
	@Override
	public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
		List<String> tip = this.tip[slotIndex];
		if(!tip.isEmpty()){
			tooltip.addAll(tip);
		}
	}
	
	public void addChanceTooltip(int index, float chance) {
		if (chance <= 0.0F) {
			tip[index].add(EnumChatFormatting.GRAY + String.format(JEIUtils.translate("chance"), JEIUtils.translate("chance.never")));
		} else if (chance < 0.01F) {
			tip[index].add(EnumChatFormatting.GRAY + String.format(JEIUtils.translate("chance"), JEIUtils.translate("chance.lessThan1")));
		} else if (chance != 1.0F) {
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMaximumFractionDigits(2);
			tip[index].add(EnumChatFormatting.GRAY + String.format(JEIUtils.translate("chance"), String.valueOf(percentFormat.format(chance))));
		}
	}

}

package forestry.database;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class DatabaseFilterToolTip extends DatabaseFilter {
	public DatabaseFilterToolTip(Pattern pattern) {
		super(pattern);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		List<ITextComponent> lines = itemStack.getTooltip(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);
		if (lines.size() <= 1) {
			return false;
		}
		lines.remove(0); // Remove the first line as that states the item name
		return lines.stream().anyMatch((t) -> pattern.matcher(t.getFormattedText()).find());
	}
}

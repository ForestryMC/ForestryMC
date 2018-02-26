package forestry.database;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

public class DatabaseFilterToolTip extends DatabaseFilter {
	public DatabaseFilterToolTip(Pattern pattern) {
		super(pattern);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		List<String> lines = itemStack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
		if (lines.size() <= 1) {
			return false;
		}
		lines.remove(0); // Remove the first line as that states the item name
		return lines.stream().anyMatch((s) -> pattern.matcher(s).find());
	}
}

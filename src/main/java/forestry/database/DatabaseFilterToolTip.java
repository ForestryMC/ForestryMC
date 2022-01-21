package forestry.database;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class DatabaseFilterToolTip extends DatabaseFilter {
	public DatabaseFilterToolTip(Pattern pattern) {
		super(pattern);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		List<Component> lines = itemStack.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
		if (lines.size() <= 1) {
			return false;
		}
		lines.remove(0); // Remove the first line as that states the item name
		return lines.stream().anyMatch((t) -> pattern.matcher(t.getString()).find());
	}
}

package forestry.core.utils;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemTooltipUtil {
	public static void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String unlocalizedName = stack.getUnlocalizedName();
		String tooltipKey = unlocalizedName + ".tooltip";
		String tooltipInfo = Translator.translateToLocal(tooltipKey);
		Minecraft minecraft = Minecraft.getMinecraft();
		List<String> tooltipInfoWrapped = minecraft.fontRendererObj.listFormattedStringToWidth(tooltipInfo, 150);
		tooltip.addAll(tooltipInfoWrapped);
	}
}

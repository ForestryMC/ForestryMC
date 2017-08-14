package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTooltipUtil {
	@SideOnly(Side.CLIENT)
	public static void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		String unlocalizedName = stack.getUnlocalizedName();
		String tooltipKey = unlocalizedName + ".tooltip";
		if (Translator.canTranslateToLocal(tooltipKey)) {
			String tooltipInfo = Translator.translateToLocal(tooltipKey);
			Minecraft minecraft = Minecraft.getMinecraft();
			List<String> tooltipInfoWrapped = minecraft.fontRenderer.listFormattedStringToWidth(tooltipInfo, 150);
			tooltip.addAll(tooltipInfoWrapped);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void addShiftInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(TextFormatting.ITALIC .toString() + '<' + Translator.translateToLocal("for.gui.tooltip.tmi") + '>');
	}
}

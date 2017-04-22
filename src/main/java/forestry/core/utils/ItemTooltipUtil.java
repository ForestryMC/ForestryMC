package forestry.core.utils;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTooltipUtil {
	@SideOnly(Side.CLIENT)
	public static void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String unlocalizedName = stack.getUnlocalizedName();
		String tooltipKey = unlocalizedName + ".tooltip";
		if (Translator.canTranslateToLocal(tooltipKey)) {
			String tooltipInfo = Translator.translateToLocal(tooltipKey);
			Minecraft minecraft = Minecraft.getMinecraft();
			List<String> tooltipInfoWrapped = minecraft.fontRendererObj.listFormattedStringToWidth(tooltipInfo, 150);
			tooltip.addAll(tooltipInfoWrapped);
		}
	}
}

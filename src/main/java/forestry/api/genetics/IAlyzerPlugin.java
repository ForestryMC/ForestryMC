package forestry.api.genetics;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAlyzerPlugin {

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage1(GuiScreen gui, ItemStack itemStack);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage2(GuiScreen gui, ItemStack itemStack);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage3(GuiScreen gui, ItemStack itemStack);

	Map<String, ItemStack> getIconStacks();

	List<String> getHints();
}

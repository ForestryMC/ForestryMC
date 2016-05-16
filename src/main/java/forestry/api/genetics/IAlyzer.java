package forestry.api.genetics;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAlyzer<II extends IIndividual, T extends ISpeciesType, G extends IGuiAlyzer, I extends IInventory> {
	
	ISpeciesRoot getSpeciesRoot();
	
	Map<String, ItemStack> getIconStacks();
	
	ResourceLocation getGuiTexture();
	
	boolean canSlotAccept(I inventory, int slotIndex, ItemStack itemStack);

	void onSlotClick(I inventory, int slotIndex, EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage1(G gui, II individual, T type);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage2(G gui, II individual, T type);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage3(G gui, II individual, T type);
	
	boolean isAlyzingFuel(ItemStack itemstack);

	T getDefaultType();

}

package forestry.core.gui;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiSizable {

	int getGuiLeft();

	int getGuiTop();

	int getSizeX();

	int getSizeY();

	Minecraft getMC();
}

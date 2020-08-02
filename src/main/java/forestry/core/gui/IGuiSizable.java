package forestry.core.gui;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuiSizable {

    int getGuiLeft();

    int getGuiTop();

    int getSizeX();

    int getSizeY();

    Minecraft getMC();
}

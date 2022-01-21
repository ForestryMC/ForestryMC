package forestry.core.gui;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuiSizable {

	int getGuiLeft();

	int getGuiTop();

	int getSizeX();

	int getSizeY();

	Minecraft getGameInstance();

	default Player getPlayer() {
		return Preconditions.checkNotNull(getGameInstance().player);
	}
}

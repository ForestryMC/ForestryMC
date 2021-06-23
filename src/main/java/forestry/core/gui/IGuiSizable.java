package forestry.core.gui;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuiSizable {

	int getGuiLeft();

	int getGuiTop();

	int getSizeX();

	int getSizeY();

	Minecraft getGameInstance();

	default PlayerEntity getPlayer() {
		return Preconditions.checkNotNull(getGameInstance().player);
	}
}

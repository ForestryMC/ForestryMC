/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiUtil;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.TextureManagerForestry;
import forestry.core.tiles.EscritoireGame;
import forestry.core.tiles.EscritoireGameToken;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class GameTokenWidget extends Widget {
    private final ItemStack HIDDEN_TOKEN = new ItemStack(Items.BOOK);

    private final EscritoireGame game;
    private final int index;

    public GameTokenWidget(EscritoireGame game, WidgetManager manager, int xPos, int yPos, int index) {
        super(manager, xPos, yPos);
        this.game = game;
        this.index = index;
    }

    @Nullable
    private EscritoireGameToken getToken() {
        return game.getToken(index);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack transform, int startY, int startX) {

        EscritoireGameToken token = getToken();
        if (token == null) {
            return;
        }

        int tokenColour = token.getTokenColour();

        float colorR = (tokenColour >> 16 & 255) / 255.0F;
        float colorG = (tokenColour >> 8 & 255) / 255.0F;
        float colorB = (tokenColour & 255) / 255.0F;

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(manager.gui.textureFile);

        //TODO not sure if this works...
        RenderSystem.enableDepthTest();
        RenderSystem.color3f(colorR, colorG, colorB);
        manager.gui.blit(transform, startX + xPos, startY + yPos, 228, 0, 22, 22);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        ItemStack tokenStack = HIDDEN_TOKEN;
        if (token.isVisible()) {
            tokenStack = token.getTokenStack();
        }

        GuiUtil.drawItemStack(manager.gui, tokenStack, startX + xPos + 3, startY + yPos + 3);

        RenderSystem.disableDepthTest();
        TextureManagerForestry.getInstance().bindGuiTextureMap();
        for (String ident : getToken().getOverlayIcons()) {
            TextureAtlasSprite icon = TextureManagerForestry.getInstance().getDefault(ident);
            //TODO no idea if this is right at all
            manager.gui.blit(transform, startX + xPos + 3, startY + yPos + 3, (int) (startX + xPos + 3 + icon.getMaxU()), (int) (startY + yPos + 3 + icon.getMaxV()), 16, 16);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        EscritoireGameToken token = getToken();
        if (token == null || !token.isVisible()) {
            return null;
        }

        ToolTip tooltip = new ToolTip();
        tooltip.add(token.getTooltip());
        return tooltip;
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
        game.choose(index);
        NetworkUtil.sendToServer(new PacketGuiSelectRequest(index, 0));
        SoundUtil.playButtonClick();
    }
}

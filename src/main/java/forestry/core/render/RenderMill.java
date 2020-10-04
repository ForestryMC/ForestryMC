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
package forestry.core.render;

import forestry.core.config.Constants;
import forestry.core.tiles.TileMill;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RenderMill implements IForestryRenderer<TileMill> {
    private enum Textures {PEDESTAL, EXTENSION, BLADE_1, BLADE_2, CHARGE}

    private final ResourceLocation[] textures;

    private final ModelRenderer pedestal;
    private final ModelRenderer column;
    private final ModelRenderer extension;
    private final ModelRenderer blade1;
    private final ModelRenderer blade2;

    public RenderMill(String baseTexture) {
        int textureWidth = 64;
        int textureHeight = 32;
        pedestal = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        pedestal.addBox(-8F, -8F, -8F, 16, 1, 16);
        pedestal.rotationPointX = 8;
        pedestal.rotationPointY = 8;
        pedestal.rotationPointZ = 8;

        column = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        column.addBox(-2, -7F, -2, 4, 15, 4);
        column.rotationPointX = 8;
        column.rotationPointY = 8;
        column.rotationPointZ = 8;

        extension = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        extension.addBox(1F, 8F, 7F, 14, 2, 2);
        extension.rotationPointX = 0;
        extension.rotationPointY = 0;
        extension.rotationPointZ = 0;

        blade1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        blade1.addBox(-4F, -5F, -3F, 8, 12, 1);
        blade1.rotationPointX = 8;
        blade1.rotationPointY = 8;
        blade1.rotationPointZ = 8;

        blade2 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        blade2.addBox(-4F, -5F, 2F, 8, 12, 1);
        blade2.rotationPointX = 8;
        blade2.rotationPointY = 8;
        blade2.rotationPointZ = 8;

        textures = new ResourceLocation[12];

        textures[Textures.PEDESTAL.ordinal()] = new ResourceLocation(Constants.MOD_ID, baseTexture + "pedestal.png");
        textures[Textures.EXTENSION.ordinal()] = new ResourceLocation(Constants.MOD_ID, baseTexture + "extension.png");
        textures[Textures.BLADE_1.ordinal()] = new ResourceLocation(Constants.MOD_ID, baseTexture + "blade1.png");
        textures[Textures.BLADE_2.ordinal()] = new ResourceLocation(Constants.MOD_ID, baseTexture + "blade2.png");

        for (int i = 0; i < 8; i++) {
            textures[Textures.CHARGE.ordinal() + i] = new ResourceLocation(
                    Constants.MOD_ID,
                    baseTexture + "column_" + i + ".png"
            );
        }
    }

    public RenderMill(String baseTexture, byte charges) {
        this(baseTexture);
    }

    @Override
    public void renderTile(TileMill tile, RenderHelper helper) {
        render(tile.progress, tile.charge, Direction.WEST, helper);
    }

    @Override
    public void renderItem(ItemStack stack, RenderHelper helper) {
        render(0.0f, 0, Direction.WEST, helper);
    }

    private void render(float progress, int charge, Direction orientation, RenderHelper helper) {

        helper.push();

        float step;

        if (progress > 0.5) {
            step = 3.99F - (progress - 0.5F) * 2F * 3.99F;
        } else {
            step = progress * 2F * 3.99F;
        }

        Vector3f rotation = new Vector3f(0, 0, 0);
        float[] translate = {0, 0, 0};
        float tfactor = step / 16;

        switch (orientation) {
            case EAST:
                // angle [2] = (float) Math.PI / 2;
                rotation.setZ((float) Math.PI);
                rotation.setY((float) -Math.PI / 2);
                translate[0] = 1;
                break;
            case WEST:
                // 2, -PI/2
                rotation.setY((float) Math.PI / 2);
                translate[0] = -1;
                break;
            case UP:
                translate[1] = 1;
                break;
            case DOWN:
                rotation.setY((float) Math.PI);
                translate[1] = -1;
                break;
            case SOUTH:
                rotation.setX((float) Math.PI / 2);
                rotation.setY((float) Math.PI / 2);
                translate[2] = 1;
                break;
            case NORTH:
            default:
                rotation.setX((float) -Math.PI / 2);
                rotation.setY((float) Math.PI / 2);
                translate[2] = -1;
                break;
        }

        helper.setRotation(rotation);

        helper.renderModel(textures[Textures.PEDESTAL.ordinal()], pedestal);

        helper.renderModel(textures[Textures.CHARGE.ordinal() + charge], column);

        Vector3f invertedRotation = rotation.copy();
        invertedRotation.mul(-1);
        helper.renderModel(textures[Textures.EXTENSION.ordinal() + charge], invertedRotation, extension);

        helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
        helper.renderModel(textures[Textures.BLADE_1.ordinal() + charge], blade1);

        // Reset
        helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

        helper.translate(-translate[0] * tfactor, translate[1] * tfactor, -translate[2] * tfactor);
        helper.renderModel(textures[Textures.BLADE_2.ordinal() + charge], blade2);

        helper.pop();

    }
}

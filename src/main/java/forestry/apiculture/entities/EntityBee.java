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
package forestry.apiculture.entities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;

public class EntityBee extends EntityCreature implements IAnimals {

	private static final String DEFAULT_TEXTURE = Constants.TEXTURE_PATH_ENTITIES + "/bees/honeyBee.png";

	private IBee contained;
	private IAlleleBeeSpecies species;
	private EnumBeeType type = EnumBeeType.DRONE;

	private String beeTexture = DEFAULT_TEXTURE;
	private long lastUpdate;

	public EntityBee(World world) {
		super(world);
	}

	private void resetAppearance() {
		beeTexture = species.getEntityTexture();
	}

	public EntityBee setIndividual(IBee bee) {
		if (bee != null) {
			contained = bee;
		} else {
			contained = BeeManager.beeRoot.templateAsIndividual(BeeManager.beeRoot.getDefaultTemplate());
		}

		//isImmuneToFire = contained.getGenome().getFireResist();
		setSpecies(contained.getGenome().getPrimary());
		//dataWatcher.updateObject(DATAWATCHER_ID_SPECIES, species.getUID());
		//dataWatcher.updateObject(DATAWATCHER_ID_SCALE, (int)(contained.getSize() * 100));
		return this;
	}

	public IBee getBee() {
		return contained;
	}

	public EntityBee setType(EnumBeeType type) {
		this.type = type;
		return this;
	}

	public EnumBeeType getType() {
		return type;
	}

	public EntityBee setSpecies(IAlleleBeeSpecies species) {
		this.species = species;
		resetAppearance();
		lastUpdate = worldObj.getTotalWorldTime();
		return this;
	}

	@SideOnly(Side.CLIENT)
	private ResourceLocation textureResource;
	private long lastTextureUpdate;

	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture() {
		if (textureResource == null || lastTextureUpdate != lastUpdate) {
			textureResource = new ForestryResource(beeTexture);
		}

		return textureResource;
	}

}

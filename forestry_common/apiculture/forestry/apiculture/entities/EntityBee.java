/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.entities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.core.config.Defaults;
import forestry.core.utils.ForestryResource;
import forestry.plugins.PluginApiculture;

public class EntityBee extends EntityCreature implements IAnimals {

	private static final String DEFAULT_TEXTURE = Defaults.TEXTURE_PATH_ENTITIES + "/bees/honeyBee.png";
	
	IBee contained;
	IAlleleBeeSpecies species;
	EnumBeeType type = EnumBeeType.DRONE;
	
    private String beeTexture = DEFAULT_TEXTURE;
    private long lastUpdate;

	public EntityBee(World world) {
		super(world);
	}

	private void resetAppearance() {
		beeTexture = species.getEntityTexture();
	}
	
	public EntityBee setIndividual(IBee bee) {
		if(bee != null)
			contained = bee;
		else
			contained = PluginApiculture.beeInterface.templateAsIndividual(PluginApiculture.beeInterface.getDefaultTemplate());
		
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
		if(textureResource == null || lastTextureUpdate != lastUpdate)
			textureResource = new ForestryResource(beeTexture);
		
		return textureResource;
	}
	
}

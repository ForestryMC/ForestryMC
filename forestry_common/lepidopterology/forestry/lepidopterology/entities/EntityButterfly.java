/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.core.IToolScoop;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.render.ButterflyItemRenderer;
import forestry.plugins.PluginLepidopterology;

public class EntityButterfly extends EntityCreature implements IEntityButterfly {

	public enum EnumButterflyState {
		FLYING(true), GLIDING(true), RESTING(false), HOVER(false);

		public static final EnumButterflyState[] VALUES = values();

		public final boolean doesMovement;

		private EnumButterflyState(boolean doesMovement) {
			this.doesMovement = doesMovement;
		}

		public float getWingFlap(EntityButterfly entity, float partialTicktime) {
			if(this == RESTING || this == HOVER) {
				long systemTime = System.currentTimeMillis();
				long flapping = systemTime + entity.contained.getIdent().hashCode();
				float flap = (float) (flapping % 1000) / 1000;   // 0 to 1

				return ButterflyItemRenderer.getIrregularWingYaw(flapping, flap);
			} else
				return entity.ticksExisted + partialTicktime;
		}
	}

	/* CONSTANTS */
	public static final int COOLDOWNS = 1000;

	private static final int DATAWATCHER_ID_SPECIES = 16;
	private static final int DATAWATCHER_ID_SCALE = 17;
	private static final int DATAWATCHER_ID_STATE = 18;

	//private static final String DEFAULT_TEXTURE = Defaults.TEXTURE_PATH_ENTITIES + "/butterflies/mothBrimstone.png";
	public static final float DEFAULT_BUTTERFLY_SCALE = 0.75f;
	private static final EnumButterflyState DEFAULT_STATE = EnumButterflyState.FLYING;

	public static final int EXHAUSTION_REST = 1000;
	public static final int EXHAUSTION_CONSUMPTION = 100 * EXHAUSTION_REST;

	private ChunkCoordinates flightTarget;
	private int exhaustion;

	private IAlleleButterflySpecies species;
	private IButterfly contained;

	private IIndividual pollen;

	private EnumButterflyState state = DEFAULT_STATE;
	private float scale  = DEFAULT_BUTTERFLY_SCALE;
	private String mothTexture;

	private long lastUpdate;

	public int cooldownPollination = 0;
	public int cooldownEgg = 0;

	/* CONSTRUCTOR */
	public EntityButterfly(World world) {
		super(world);
		setDefaults(PluginLepidopterology.butterflyInterface.templateAsIndividual(PluginLepidopterology.butterflyInterface.getRandomTemplate(rand)));
	}

	public EntityButterfly(World world, IButterfly butterfly) {
		super(world);
		setDefaults(butterfly);
	}

	private void setDefaults(IButterfly butterfly) {
		contained = butterfly;
		tasks.addTask(10, new AIButterflyWander(this));
		tasks.addTask(9, new AIButterflyRest(this));
		tasks.addTask(8, new AIButterflyInteract(this));
		tasks.addTask(8, new AIButterflyFlee(this));
	}

	@Override
	public EntityCreature getEntity() {
		return this;
	}

	/* SAVING & LOADING */
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);

		NBTTagCompound bio = new NBTTagCompound();
		contained.writeToNBT(bio);
		nbttagcompound.setTag("BTFLY", bio);

		if(pollen != null) {
			NBTTagCompound pln = new NBTTagCompound();
			pollen.writeToNBT(pln);
			nbttagcompound.setTag("PLN", pln);
		}

		nbttagcompound.setByte("STATE", (byte)state.ordinal());
		nbttagcompound.setInteger("EXH", exhaustion);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		if(nbttagcompound.hasKey("BTFLY")) {
			contained = new Butterfly((NBTTagCompound)nbttagcompound.getTag("BTFLY"));
		}
		setIndividual(contained);

		if(nbttagcompound.hasKey("PLN")) {
			pollen = AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees").getMember((NBTTagCompound)nbttagcompound.getTag("PLN"));
		}

		state = EnumButterflyState.VALUES[nbttagcompound.getByte("STATE")];
		exhaustion = nbttagcompound.getInteger("EXH");
	}

	public float getWingFlap(float partialTicktime) {
		return state.getWingFlap(this, partialTicktime);
	}

	public float getScale() {
		return scale;
	}

	// Client side helper to set size for rendering if necessary.
	public void setScale(float size) {
		scale = size;
	}

	public float getSpeed() {
		return contained.getGenome().getSpeed();
	}

	/* DESTINATION */
	public ChunkCoordinates getDestination() {
		return flightTarget;
	}

	public void setDestination(ChunkCoordinates destination) {
		flightTarget = destination;
	}

	/* POLLEN */
	@Override
	public IIndividual getPollen() {
		return pollen;
	}

	@Override
	public void setPollen(IIndividual pollen) {
		this.pollen = pollen;
	}

	/* STATE - Used for rendering */
	public EnumButterflyState getState() {
		return state;
	}

	public void setState(EnumButterflyState state) {
		if(state != this.state) {
			this.state = state;
			dataWatcher.updateObject(DATAWATCHER_ID_STATE, (byte)state.ordinal());
		}
	}

	public boolean isRenderable() {
		return species != null;
	}

	/* EXHAUSTION */
	@Override
	public void changeExhaustion(int change) {
		exhaustion = exhaustion + change > 0 ? exhaustion + change : 0;
	}

	@Override
	public int getExhaustion() {
		return exhaustion;
	}

	/* FLYING ABILITY */
	public boolean canFly() {
		return contained.canTakeFlight(worldObj, posX, posY, posZ);
	}

	/* SETTING GENETIC INDIVIDUAL */
	private void resetAppearance() {
		mothTexture = species.getEntityTexture();
		setSize(getScale(), 0.2f);
	}

	public EntityButterfly setIndividual(IButterfly butterfly) {
		if(butterfly != null)
			contained = butterfly;
		else
			contained = PluginLepidopterology.butterflyInterface.templateAsIndividual(PluginLepidopterology.butterflyInterface.getDefaultTemplate());

		isImmuneToFire = contained.getGenome().getFireResist();
		setSpecies(contained.getGenome().getPrimary());
		dataWatcher.updateObject(DATAWATCHER_ID_SPECIES, species.getUID());
		dataWatcher.updateObject(DATAWATCHER_ID_SCALE, (int)(contained.getSize() * 100));
		return this;
	}

	@Override
	public IButterfly getButterfly() {
		return contained;
	}

	public EntityButterfly setSpecies(IAlleleButterflySpecies butterfly) {
		species = butterfly;
		resetAppearance();
		lastUpdate = worldObj.getTotalWorldTime();
		return this;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		if(Proxies.common.isSimulating(worldObj))
			setIndividual(contained);
		return data;
	}
	@Override public boolean getCanSpawnHere() { return true; }

	@Override
	protected void entityInit() {
		super.entityInit();

		if(state == null)
			state = DEFAULT_STATE;

		dataWatcher.addObject(DATAWATCHER_ID_SPECIES, "");
		dataWatcher.addObject(DATAWATCHER_ID_SCALE, 75);
		dataWatcher.addObject(DATAWATCHER_ID_STATE, (byte)state.ordinal());
	}

	@Override
	public int getMaxInPortalTime() {
		return 1000;
	}

	@SideOnly(Side.CLIENT)
	private ResourceLocation textureResource;
	private long lastTextureUpdate;
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture() {
		if(textureResource == null || lastTextureUpdate != lastUpdate)
			textureResource = new ResourceLocation(mothTexture);

		return textureResource;
	}

	@Override public boolean canBePushed() { return false; }
	@Override protected void collideWithEntity(Entity other) {}
	@Override protected boolean isAIEnabled() { return true; }
	@Override protected boolean canDespawn() { return false; }

	/* INTERACTION */
	@Override
	public boolean interact(EntityPlayer player) {
		if(isDead)
			return false;
		if(player.getHeldItem() == null)
			return false;
		if(!(player.getHeldItem().getItem() instanceof IToolScoop))
			return false;

		if(Proxies.common.isSimulating(worldObj)) {
			contained.getGenome().getPrimary().getRoot().getBreedingTracker(worldObj, player.getGameProfile()).registerCatch(contained);
			StackUtils.dropItemStackAsEntity(PluginLepidopterology.butterflyInterface.getMemberStack(contained.copy(), EnumFlutterType.BUTTERFLY.ordinal()), worldObj, posX, posY, posZ);
			setDead();
		} else
			player.swingItem();
		return true;
	}

	/* LOOT */
	@Override
	protected void dropFewItems(boolean playerKill, int lootLevel) {
		for(ItemStack stack : contained.getLootDrop(this, playerKill, lootLevel))
			StackUtils.dropItemStackAsEntity(stack, worldObj, posX, posY, posZ);

		// Drop pollen if any
		if(getPollen() != null) {
			StackUtils.dropItemStackAsEntity(AlleleManager.alleleRegistry.getSpeciesRoot(getPollen().getClass()).getMemberStack(getPollen(), EnumGermlingType.POLLEN.ordinal()), worldObj, posX, posY, posZ);
		}
	}

	/* UPDATING */
	@Override
	public void onUpdate() {
		super.onUpdate();

		// Update stuff client side
		if(!Proxies.common.isSimulating(worldObj)) {
			if (species == null || dataWatcher.hasChanges()) {
				scale = (float)dataWatcher.getWatchableObjectInt(DATAWATCHER_ID_SCALE) / 100;
				String uid = dataWatcher.getWatchableObjectString(DATAWATCHER_ID_SPECIES);
				if(species == null || !species.getUID().equals(uid)) {
					IAllele allele = AlleleManager.alleleRegistry.getAllele(uid);
					if(allele instanceof IAlleleButterflySpecies) {
						setSpecies((IAlleleButterflySpecies)allele);
					}
				}
			}
			state = EnumButterflyState.VALUES[dataWatcher.getWatchableObjectByte(DATAWATCHER_ID_STATE)];
		}

		motionY *= 0.6000000238418579d;

		// Make sure we die if the butterfly hasn't rested in a long, long time.
		if(exhaustion > EXHAUSTION_CONSUMPTION && getRNG().nextInt(20) == 0)
			attackEntityFrom(DamageSource.generic, 1);

		// Reduce cooldowns
		if(cooldownEgg > 0) cooldownEgg--;
		if(cooldownPollination > 0) cooldownPollination--;
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if(state.doesMovement && flightTarget != null) {
			double diffX = flightTarget.posX + 0.5d - posX;
			double diffY = flightTarget.posY + 0.1d - posY;
			double diffZ = flightTarget.posZ + 0.5d - posZ;

			motionX += (Math.signum(diffX) * 0.5d - motionX) * 0.10000000149011612d;
			motionY += (Math.signum(diffY) * 0.699999988079071d - motionY) * 0.10000000149011612d;
			motionZ += (Math.signum(diffZ) * 0.5d - motionZ) * 0.10000000149011612d;

			//float f = (float)(Math.atan2(motionZ, motionX) * 180.0d / Math.PI) - 90f;
			//rotationYaw += MathHelper.wrapAngleTo180_float(f - rotationYaw);
			float horizontal = (float)((Math.atan2(motionZ, motionX) * 180d) / Math.PI) - 90f;
			rotationYaw += MathHelper.wrapAngleTo180_float(horizontal - rotationYaw);

			setMoveForward(contained.getGenome().getSpeed());
		}

	}

	@Override protected boolean canTriggerWalking() { return false; }
	@Override protected void fall(float par1) {}
	@Override protected void updateFallState(double par1, boolean par3) {}
	@Override public boolean doesEntityNotTriggerPressurePlate() { return true; }

	@Override
	public float getSwingProgress(float partialTicktime) {
		float flap = swingProgress - prevSwingProgress;
		if (flap < 0.0F)
			++flap;

		return prevSwingProgress + flap * partialTicktime;

	}

}

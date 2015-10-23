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
package forestry.lepidopterology.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.IPlantable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IToolScoop;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.render.RenderButterflyItem;

public class EntityButterfly extends EntityCreature implements IEntityButterfly {

	public enum EnumButterflyState {

		FLYING(true), GLIDING(true), RISING(true), RESTING(false), HOVER(false);

		public static final EnumButterflyState[] VALUES = values();

		public final boolean doesMovement;

		EnumButterflyState(boolean doesMovement) {
			this.doesMovement = doesMovement;
		}

		public float getWingFlap(EntityButterfly entity, float partialTicktime) {
			if (this == RESTING || this == HOVER) {
				long systemTime = System.currentTimeMillis();
				long flapping = systemTime + entity.contained.getIdent().hashCode();
				float flap = (float) (flapping % 1000) / 1000;   // 0 to 1

				return RenderButterflyItem.getIrregularWingYaw(flapping, flap);
			} else {
				return entity.ticksExisted + partialTicktime;
			}
		}
	}

	/* CONSTANTS */
	public static final int COOLDOWNS = 1500;

	private static final int DATAWATCHER_ID_SPECIES = 16;
	private static final int DATAWATCHER_ID_SCALE = 17;
	private static final int DATAWATCHER_ID_STATE = 18;

	//private static final String DEFAULT_TEXTURE = Constants.TEXTURE_PATH_ENTITIES + "/butterflies/mothBrimstone.png";
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
	private float scale = DEFAULT_BUTTERFLY_SCALE;
	private String mothTexture;

	private long lastUpdate;

	public int cooldownPollination = 0;
	public int cooldownEgg = 0;

	private int lifespanRemaining = 24000 * 7; // one minecraft week in ticks

	/* CONSTRUCTOR */
	public EntityButterfly(World world) {
		super(world);
		setDefaults(ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getRandomTemplate(rand)));
	}

	public EntityButterfly(World world, IButterfly butterfly) {
		super(world);
		setDefaults(butterfly);
	}

	private void setDefaults(IButterfly butterfly) {
		contained = butterfly;
		tasks.addTask(8, new AIButterflyFlee(this));
		tasks.addTask(9, new AIButterflyMate(this));
		tasks.addTask(10, new AIButterflyPollinate(this));
		tasks.addTask(11, new AIButterflyRest(this));
		tasks.addTask(12, new AIButterflyRise(this));
		tasks.addTask(12, new AIButterflyWander(this));
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

		if (pollen != null) {
			NBTTagCompound pln = new NBTTagCompound();
			pollen.writeToNBT(pln);
			nbttagcompound.setTag("PLN", pln);
		}

		nbttagcompound.setByte("STATE", (byte) state.ordinal());
		nbttagcompound.setInteger("EXH", exhaustion);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("BTFLY")) {
			contained = new Butterfly((NBTTagCompound) nbttagcompound.getTag("BTFLY"));
		}
		setIndividual(contained);

		if (nbttagcompound.hasKey("PLN")) {
			pollen = TreeManager.treeRoot.getMember((NBTTagCompound) nbttagcompound.getTag("PLN"));
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

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		float weight = 0.0f;

		if (!getButterfly().isAcceptedEnvironment(worldObj, x, y, z)) {
			weight -= 15.0f;
		}

		if (!worldObj.getEntitiesWithinAABB(EntityButterfly.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1)).isEmpty()) {
			weight -= 1.0f;
		}

		int depth = getFluidDepth(x, z);
		if (depth > 0) {
			weight -= 0.1f * depth;
		} else {
			Block block = worldObj.getBlock(x, y, z);
			if (block instanceof BlockFlower) {
				weight += 2.0f;
			} else if (block instanceof IPlantable) {
				weight += 1.5f;
			} else if (block instanceof IGrowable) {
				weight += 1.0f;
			} else if (block.getMaterial() == Material.plants) {
				weight += 1.0f;
			}

			block = worldObj.getBlock(x, y - 1, z);
			if (block.isLeaves(worldObj, x, y - 1, z)) {
				weight += 2.5f;
			} else if (block instanceof BlockFence) {
				weight += 1.0f;
			} else if (block instanceof BlockWall) {
				weight += 1.0f;
			}
		}

		weight += worldObj.getLightBrightness(x, y, z);
		return weight;
	}

	private int getFluidDepth(int x, int z) {
		Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
		int xx = x & 15;
		int zz = z & 15;
		int depth = 0;
		for (int y = chunk.getTopFilledSegment() + 15; y > 0; --y) {
			Block block = chunk.getBlock(xx, y, zz);
			if (block.getMaterial().isLiquid()) {
				depth++;
			} else if (!block.isAir(worldObj, x, y, z)) {
				break;
			}
		}

		return depth;
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
		if (state != this.state) {
			this.state = state;
			dataWatcher.updateObject(DATAWATCHER_ID_STATE, (byte) state.ordinal());
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

	private EntityButterfly setIndividual(IButterfly butterfly) {
		if (butterfly != null) {
			contained = butterfly;
		} else {
			contained = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}

		isImmuneToFire = contained.getGenome().getFireResist();
		setSpecies(contained.getGenome().getPrimary());
		dataWatcher.updateObject(DATAWATCHER_ID_SPECIES, species.getUID());
		dataWatcher.updateObject(DATAWATCHER_ID_SCALE, (int) (contained.getSize() * 100));
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
		if (!worldObj.isRemote) {
			setIndividual(contained);
		}
		return data;
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		if (state == null) {
			state = DEFAULT_STATE;
		}

		dataWatcher.addObject(DATAWATCHER_ID_SPECIES, "");
		dataWatcher.addObject(DATAWATCHER_ID_SCALE, 75);
		dataWatcher.addObject(DATAWATCHER_ID_STATE, (byte) state.ordinal());
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
		if (textureResource == null || lastTextureUpdate != lastUpdate) {
			textureResource = new ResourceLocation(mothTexture);
		}

		return textureResource;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity other) {
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	/* INTERACTION */
	@Override
	public boolean interact(EntityPlayer player) {
		if (isDead) {
			return false;
		}
		if (player.getHeldItem() == null) {
			return false;
		}
		if (!(player.getHeldItem().getItem() instanceof IToolScoop)) {
			return false;
		}

		if (!worldObj.isRemote) {
			contained.getGenome().getPrimary().getRoot().getBreedingTracker(worldObj, player.getGameProfile()).registerCatch(contained);
			ItemStackUtil.dropItemStackAsEntity(ButterflyManager.butterflyRoot.getMemberStack(contained.copy(), EnumFlutterType.BUTTERFLY.ordinal()), worldObj, posX, posY, posZ);
			setDead();
		} else {
			player.swingItem();
		}
		return true;
	}

	/* LOOT */
	@Override
	protected void dropFewItems(boolean playerKill, int lootLevel) {
		for (ItemStack stack : contained.getLootDrop(this, playerKill, lootLevel)) {
			ItemStackUtil.dropItemStackAsEntity(stack, worldObj, posX, posY, posZ);
		}

		// Drop pollen if any
		if (getPollen() != null) {
			ItemStackUtil.dropItemStackAsEntity(AlleleManager.alleleRegistry.getSpeciesRoot(getPollen().getClass()).getMemberStack(getPollen(), EnumGermlingType.POLLEN.ordinal()), worldObj, posX, posY, posZ);
		}
	}

	/* UPDATING */
	@Override
	public void onUpdate() {
		super.onUpdate();

		// Update stuff client side
		if (worldObj.isRemote) {
			if (species == null || dataWatcher.hasChanges()) {
				scale = (float) dataWatcher.getWatchableObjectInt(DATAWATCHER_ID_SCALE) / 100;
				String uid = dataWatcher.getWatchableObjectString(DATAWATCHER_ID_SPECIES);
				if (species == null || !species.getUID().equals(uid)) {
					IAllele allele = AlleleManager.alleleRegistry.getAllele(uid);
					if (allele instanceof IAlleleButterflySpecies) {
						setSpecies((IAlleleButterflySpecies) allele);
					}
				}
			}
			state = EnumButterflyState.VALUES[dataWatcher.getWatchableObjectByte(DATAWATCHER_ID_STATE)];
		}

		motionY *= 0.6000000238418579d;

		// Make sure we die if the butterfly hasn't rested in a long, long time.
		if (exhaustion > EXHAUSTION_CONSUMPTION && getRNG().nextInt(20) == 0) {
			attackEntityFrom(DamageSource.generic, 1);
		}

		if (lifespanRemaining > 0) {
			lifespanRemaining--;
		} else {
			attackEntityFrom(DamageSource.generic, 1);
		}

		// Reduce cooldowns
		if (cooldownEgg > 0) {
			cooldownEgg--;
		}
		if (cooldownPollination > 0) {
			cooldownPollination--;
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (state.doesMovement && flightTarget != null) {
			double diffX = flightTarget.posX + 0.5d - posX;
			double diffY = flightTarget.posY + 0.1d - posY;
			double diffZ = flightTarget.posZ + 0.5d - posZ;

			motionX += (Math.signum(diffX) * 0.5d - motionX) * 0.10000000149011612d;
			motionY += (Math.signum(diffY) * 0.699999988079071d - motionY) * 0.10000000149011612d;
			motionZ += (Math.signum(diffZ) * 0.5d - motionZ) * 0.10000000149011612d;

			//float f = (float)(Math.atan2(motionZ, motionX) * 180.0d / Math.PI) - 90f;
			//rotationYaw += MathHelper.wrapAngleTo180_float(f - rotationYaw);
			float horizontal = (float) ((Math.atan2(motionZ, motionX) * 180d) / Math.PI) - 90f;
			rotationYaw += MathHelper.wrapAngleTo180_float(horizontal - rotationYaw);

			setMoveForward(contained.getGenome().getSpeed());
		}

	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void fall(float par1) {
	}

	@Override
	protected void updateFallState(double par1, boolean par3) {
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@Override
	public float getSwingProgress(float partialTicktime) {
		float flap = swingProgress - prevSwingProgress;
		if (flap < 0.0F) {
			++flap;
		}

		return prevSwingProgress + flap * partialTicktime;

	}

	@Override
	protected float getSoundVolume() {
		return 0.1F;
	}

}

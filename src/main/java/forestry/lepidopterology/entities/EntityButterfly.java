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
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
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
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.genetics.Butterfly;

public class EntityButterfly extends EntityCreature implements IEntityButterfly {

	/* CONSTANTS */
	public static final int COOLDOWNS = 1500;

	private static final int DATAWATCHER_ID_SPECIES = 16;
	private static final int DATAWATCHER_ID_SIZE = 17;
	private static final int DATAWATCHER_ID_STATE = 18;

	private static final float DEFAULT_BUTTERFLY_SIZE = 0.75f;
	private static final EnumButterflyState DEFAULT_STATE = EnumButterflyState.FLYING;

	public static final int EXHAUSTION_REST = 1000;
	public static final int EXHAUSTION_CONSUMPTION = 100 * EXHAUSTION_REST;
	public static final int MAX_LIFESPAN = 24000 * 7; // one minecraft week in ticks

	private Vec3 flightTarget;
	private int exhaustion;
	private IButterfly contained;
	private IIndividual pollen;

	public int cooldownPollination = 0;
	public int cooldownEgg = 0;

	// Client Rendering
	private IAlleleButterflySpecies species;
	private float size = DEFAULT_BUTTERFLY_SIZE;
	private EnumButterflyState state = DEFAULT_STATE;

	/* CONSTRUCTOR */
	public EntityButterfly(World world) {
		super(world);
		setDefaults();
	}

	public EntityButterfly(World world, IButterfly butterfly) {
		super(world);
		setDefaults();
		setIndividual(butterfly);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataWatcher.addObject(DATAWATCHER_ID_SPECIES, "");
		dataWatcher.addObject(DATAWATCHER_ID_SIZE, (int) (DEFAULT_BUTTERFLY_SIZE * 100));
		dataWatcher.addObject(DATAWATCHER_ID_STATE, (byte) DEFAULT_STATE.ordinal());
	}

	private void setDefaults() {
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

		nbttagcompound.setByte("STATE", (byte) getState().ordinal());
		nbttagcompound.setInteger("EXH", exhaustion);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		IButterfly butterfly = null;
		if (nbttagcompound.hasKey("BTFLY")) {
			butterfly = new Butterfly((NBTTagCompound) nbttagcompound.getTag("BTFLY"));
		}
		setIndividual(butterfly);

		if (nbttagcompound.hasKey("PLN")) {
			pollen = TreeManager.treeRoot.getMember((NBTTagCompound) nbttagcompound.getTag("PLN"));
		}

		EnumButterflyState state = EnumButterflyState.VALUES[nbttagcompound.getByte("STATE")];
		setState(state);
		exhaustion = nbttagcompound.getInteger("EXH");
	}

	public float getWingFlap(float partialTicktime) {
		return getState().getWingFlap(this, species.getUID().hashCode(), partialTicktime);
	}

	/* STATE - Used for AI and rendering */
	public void setState(EnumButterflyState state) {
		if (this.state != state) {
			this.state = state;
			if (!worldObj.isRemote) {
				dataWatcher.updateObject(DATAWATCHER_ID_STATE, (byte) state.ordinal());
			}
		}
	}

	public EnumButterflyState getState() {
		return state;
	}

	public float getSize() {
		return size;
	}

	public float getSpeed() {
		return contained.getGenome().getSpeed();
	}

	/* DESTINATION */
	public Vec3 getDestination() {
		return flightTarget;
	}

	public void setDestination(Vec3 destination) {
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

	public void setIndividual(IButterfly butterfly) {
		if (butterfly == null) {
			butterfly = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}
		contained = butterfly;

		IButterflyGenome genome = contained.getGenome();

		isImmuneToFire = genome.getFireResist();
		size = genome.getSize();
		setSize(size, 0.4f);
		species = genome.getPrimary();

		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_ID_SIZE, (int) (size * 100));
			dataWatcher.updateObject(DATAWATCHER_ID_SPECIES, species.getUID());
		} else {
			textureResource = new ResourceLocation(species.getEntityTexture());
		}
	}

	@Override
	public IButterfly getButterfly() {
		return contained;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		if (!worldObj.isRemote) {
			setIndividual(contained);
		}
		return data;
	}

	@Override
	public String getCommandSenderName() {
		if (species == null) {
			return super.getCommandSenderName();
		}
		return species.getName();
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	public int getMaxInPortalTime() {
		return 1000;
	}

	@SideOnly(Side.CLIENT)
	private ResourceLocation textureResource;

	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture() {
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
		return ticksExisted > MAX_LIFESPAN;
	}

	/* INTERACTION */
	@Override
	public boolean interact(EntityPlayer player) {
		if (isDead) {
			return false;
		}

		ItemStack heldItem = player.getHeldItem();
		if (heldItem == null) {
			return false;
		}

		if (!(heldItem.getItem() instanceof IToolScoop)) {
			return false;
		}

		if (!worldObj.isRemote) {
			IButterflyRoot root = contained.getGenome().getPrimary().getRoot();
			ILepidopteristTracker tracker = root.getBreedingTracker(worldObj, player.getGameProfile());
			ItemStack itemStack = root.getMemberStack(contained.copy(), EnumFlutterType.BUTTERFLY.ordinal());

			tracker.registerCatch(contained);
			ItemStackUtil.dropItemStackAsEntity(itemStack, worldObj, posX, posY, posZ);
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
		IIndividual pollen = getPollen();
		if (pollen != null) {
			ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(pollen.getClass());
			ItemStack pollenStack = root.getMemberStack(pollen, EnumGermlingType.POLLEN.ordinal());
			ItemStackUtil.dropItemStackAsEntity(pollenStack, worldObj, posX, posY, posZ);
		}
	}

	/* UPDATING */
	@Override
	public void onUpdate() {
		super.onUpdate();

		// Update stuff client side
		if (worldObj.isRemote) {
			if (species == null) {
				String speciesUid = dataWatcher.getWatchableObjectString(DATAWATCHER_ID_SPECIES);
				IAllele allele = AlleleManager.alleleRegistry.getAllele(speciesUid);
				if (allele instanceof IAlleleButterflySpecies) {
					species = (IAlleleButterflySpecies) allele;
					textureResource = new ResourceLocation(species.getEntityTexture());
					size = dataWatcher.getWatchableObjectInt(DATAWATCHER_ID_SIZE) / 100f;
				}
			}

			int stateOrdinal = dataWatcher.getWatchableObjectByte(DATAWATCHER_ID_STATE);
			if (state == null || state.ordinal() != stateOrdinal) {
				setState(EnumButterflyState.VALUES[stateOrdinal]);
			}
		}

		motionY *= 0.6000000238418579d;

		// Make sure we die if the butterfly hasn't rested in a long, long time.
		if (exhaustion > EXHAUSTION_CONSUMPTION && getRNG().nextInt(20) == 0) {
			attackEntityFrom(DamageSource.generic, 1);
		}

		if (ticksExisted > MAX_LIFESPAN) {
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

		if (getState().doesMovement && flightTarget != null) {
			double diffX = flightTarget.xCoord + 0.5d - posX;
			double diffY = flightTarget.yCoord + 0.1d - posY;
			double diffZ = flightTarget.zCoord + 0.5d - posZ;

			motionX += (Math.signum(diffX) * 0.5d - motionX) * 0.10000000149011612d;
			motionY += (Math.signum(diffY) * 0.699999988079071d - motionY) * 0.10000000149011612d;
			motionZ += (Math.signum(diffZ) * 0.5d - motionZ) * 0.10000000149011612d;

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

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		if (species == null) {
			return null;
		}
		IButterflyRoot root = species.getRoot();
		IAllele[] template = root.getTemplate(species.getUID());
		IButterfly butterfly = root.templateAsIndividual(template);
		return root.getMemberStack(butterfly, EnumFlutterType.BUTTERFLY.ordinal());
	}
}

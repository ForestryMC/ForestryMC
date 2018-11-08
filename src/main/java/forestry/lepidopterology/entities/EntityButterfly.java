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

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.IPlantable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.genetics.Butterfly;

public class EntityButterfly extends EntityCreature implements IEntityButterfly {

	/* CONSTANTS */
	public static final int COOLDOWNS = 1500;

	private static final DataParameter<String> DATAWATCHER_ID_SPECIES = EntityDataManager.createKey(EntityButterfly.class, DataSerializers.STRING);
	private static final DataParameter<Integer> DATAWATCHER_ID_SIZE = EntityDataManager.createKey(EntityButterfly.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> DATAWATCHER_ID_STATE = EntityDataManager.createKey(EntityButterfly.class, DataSerializers.BYTE);

	private static final float DEFAULT_BUTTERFLY_SIZE = 0.75f;
	private static final EnumButterflyState DEFAULT_STATE = EnumButterflyState.FLYING;

	public static final int EXHAUSTION_REST = 1000;
	public static final int EXHAUSTION_CONSUMPTION = 100 * EXHAUSTION_REST;
	public static final int MAX_LIFESPAN = 24000 * 7; // one minecraft week in ticks

	@Nullable
	private Vec3d flightTarget;
	private int exhaustion;
	private IButterfly contained = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
	@Nullable
	private IIndividual pollen;

	public int cooldownPollination = 0;
	public int cooldownEgg = 0;
	public int cooldownMate = 0;

	// Client Rendering
	@Nullable
	private IAlleleButterflySpecies species;
	private float size = DEFAULT_BUTTERFLY_SIZE;
	private EnumButterflyState state = DEFAULT_STATE;
	@SideOnly(Side.CLIENT)
	private ResourceLocation textureResource;

	/* CONSTRUCTOR */
	public EntityButterfly(World world) {
		super(world);
		setDefaults();
	}

	public EntityButterfly(World world, IButterfly butterfly, BlockPos homePos) {
		super(world);
		setDefaults();
		setIndividual(butterfly);
		setHomePosAndDistance(homePos, ModuleLepidopterology.maxDistance);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(DATAWATCHER_ID_SPECIES, "");
		dataManager.register(DATAWATCHER_ID_SIZE, (int) (DEFAULT_BUTTERFLY_SIZE * 100));
		dataManager.register(DATAWATCHER_ID_STATE, (byte) DEFAULT_STATE.ordinal());
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
			pln.setString("Root", pollen.getGenome().getSpeciesRoot().getUID());
			pollen.writeToNBT(pln);
			nbttagcompound.setTag("PLN", pln);
		}

		nbttagcompound.setByte("STATE", (byte) getState().ordinal());
		nbttagcompound.setInteger("EXH", exhaustion);

		nbttagcompound.setInteger("homeX", getHomePosition().getX());
		nbttagcompound.setInteger("homeY", getHomePosition().getY());
		nbttagcompound.setInteger("homeZ", getHomePosition().getZ());
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
			NBTTagCompound pollenNBT = nbttagcompound.getCompoundTag("PLN");
			ISpeciesRoot root;
			if (pollenNBT.hasKey("Root")) {
				root = AlleleManager.alleleRegistry.getSpeciesRoot(pollenNBT.getString("Root"));
			} else {
				root = TreeManager.treeRoot;
			}
			pollen = root.getMember(pollenNBT);
		}

		EnumButterflyState state = EnumButterflyState.VALUES[nbttagcompound.getByte("STATE")];
		setState(state);
		exhaustion = nbttagcompound.getInteger("EXH");
		BlockPos home = new BlockPos(nbttagcompound.getInteger("homeX"), nbttagcompound.getInteger("homeY"), nbttagcompound.getInteger("homeZ"));
		setHomePosAndDistance(home, ModuleLepidopterology.maxDistance);
	}

	public float getWingFlap(float partialTicktime) {
		int offset = species != null ? species.getUID().hashCode() : world.rand.nextInt();
		return getState().getWingFlap(this, offset, partialTicktime);
	}

	/* STATE - Used for AI and rendering */
	public void setState(EnumButterflyState state) {
		if (this.state != state) {
			this.state = state;
			if (!world.isRemote) {
				dataManager.set(DATAWATCHER_ID_STATE, (byte) state.ordinal());
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
	@Nullable
	public Vec3d getDestination() {
		return flightTarget;
	}

	public void setDestination(@Nullable Vec3d destination) {
		flightTarget = destination;
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return -100f;
		}

		float weight = 0.0f;
		double distanceToHome = getHomePosition().distanceSq(pos);

		if (!isWithinHomeDistanceFromPosition(distanceToHome)) {

			weight -= 7.5f + 0.005 * (distanceToHome / 4);
		}

		if (!getButterfly().isAcceptedEnvironment(world, pos.getX(), pos.getY(), pos.getZ())) {
			weight -= 15.0f;
		}

		if (!world.getEntitiesWithinAABB(EntityButterfly.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).isEmpty()) {
			weight -= 1.0f;
		}

		int depth = getFluidDepth(pos);
		if (depth > 0) {
			weight -= 0.1f * depth;
		} else {
			IBlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block instanceof BlockFlower) {
				weight += 2.0f;
			} else if (block instanceof IPlantable) {
				weight += 1.5f;
			} else if (block instanceof IGrowable) {
				weight += 1.0f;
			} else if (blockState.getMaterial() == Material.PLANTS) {
				weight += 1.0f;
			}

			BlockPos posBelow = pos.down();
			IBlockState blockStateBelow = world.getBlockState(posBelow);
			Block blockBelow = blockStateBelow.getBlock();
			if (blockBelow.isLeaves(blockStateBelow, world, posBelow)) {
				weight += 2.5f;
			} else if (blockBelow instanceof BlockFence) {
				weight += 1.0f;
			} else if (blockBelow instanceof BlockWall) {
				weight += 1.0f;
			}
		}

		weight += world.getLightBrightness(pos);
		return weight;
	}

	private boolean isWithinHomeDistanceFromPosition(double distanceToHome) {
		return distanceToHome < this.getMaximumHomeDistance() * this.getMaximumHomeDistance();
	}

	private int getFluidDepth(BlockPos pos) {
		Chunk chunk = world.getChunk(pos);
		int xx = pos.getX() & 15;
		int zz = pos.getZ() & 15;
		int depth = 0;
		for (int y = chunk.getTopFilledSegment() + 15; y > 0; --y) {
			IBlockState blockState = chunk.getBlockState(xx, y, zz);
			Block block = blockState.getBlock();
			if (blockState.getMaterial().isLiquid()) {
				depth++;
			} else if (!block.isAir(blockState, world, pos)) {
				break;
			}
		}

		return depth;
	}

	/* POLLEN */
	@Override
	@Nullable
	public IIndividual getPollen() {
		return pollen;
	}

	@Override
	public void setPollen(@Nullable IIndividual pollen) {
		this.pollen = pollen;
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
		return contained.canTakeFlight(world, posX, posY, posZ);
	}

	public void setIndividual(@Nullable IButterfly butterfly) {
		if (butterfly == null) {
			butterfly = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}
		contained = butterfly;

		IButterflyGenome genome = contained.getGenome();

		isImmuneToFire = genome.getFireResist();
		size = genome.getSize();
		setSize(size, 0.4f);
		species = genome.getPrimary();

		if (!world.isRemote) {
			dataManager.set(DATAWATCHER_ID_SIZE, (int) (size * 100));
			dataManager.set(DATAWATCHER_ID_SPECIES, species.getUID());
		} else {
			textureResource = new ResourceLocation(species.getEntityTexture());
		}
	}

	@Override
	public IButterfly getButterfly() {
		return contained;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
		if (!world.isRemote) {
			setIndividual(contained);
		}
		return data;
	}

	@Override
	public String getName() {
		if (species == null) {
			return super.getName();
		}
		return species.getAlleleName();
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	public int getMaxInPortalTime() {
		return 1000;
	}

	public boolean isRenderable() {
		return species != null;
	}

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
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return ticksExisted > MAX_LIFESPAN;
	}

	/* INTERACTION */

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (isDead) {
			return false;
		}

		ItemStack stack = player.getHeldItem(hand);
		if ((stack.getItem() instanceof IToolScoop)) {
			if (!world.isRemote) {
				IButterflyRoot root = contained.getGenome().getPrimary().getRoot();
				ILepidopteristTracker tracker = root.getBreedingTracker(world, player.getGameProfile());
				ItemStack itemStack = root.getMemberStack(contained.copy(), EnumFlutterType.BUTTERFLY);

				tracker.registerCatch(contained);
				ItemStackUtil.dropItemStackAsEntity(itemStack, world, posX, posY, posZ);
				setDead();
			} else {
				player.swingArm(hand);
			}
			return true;
		}

		return false;
	}

	/* LOOT */
	@Override
	protected void dropFewItems(boolean playerKill, int lootLevel) {
		for (ItemStack stack : contained.getLootDrop(this, playerKill, lootLevel)) {
			ItemStackUtil.dropItemStackAsEntity(stack, world, posX, posY, posZ);
		}

		// Drop pollen if any
		IIndividual pollen = getPollen();
		if (pollen != null) {
			ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(pollen);
			ItemStack pollenStack = root.getMemberStack(pollen, EnumGermlingType.POLLEN);
			ItemStackUtil.dropItemStackAsEntity(pollenStack, world, posX, posY, posZ);
		}
	}

	/* UPDATING */
	@Override
	public void onUpdate() {
		super.onUpdate();

		// Update stuff client side
		if (world.isRemote) {
			if (species == null) {
				String speciesUid = dataManager.get(DATAWATCHER_ID_SPECIES);
				IAllele allele = AlleleManager.alleleRegistry.getAllele(speciesUid);
				if (allele instanceof IAlleleButterflySpecies) {
					species = (IAlleleButterflySpecies) allele;
					textureResource = new ResourceLocation(species.getEntityTexture());
					size = dataManager.get(DATAWATCHER_ID_SIZE) / 100f;
				}
			}

			byte stateOrdinal = dataManager.get(DATAWATCHER_ID_STATE);
			if (state.ordinal() != stateOrdinal) {
				setState(EnumButterflyState.VALUES[stateOrdinal]);
			}
		}

		motionY *= 0.6000000238418579d;

		// Make sure we die if the butterfly hasn't rested in a long, long time.
		if (exhaustion > EXHAUSTION_CONSUMPTION && getRNG().nextInt(20) == 0) {
			attackEntityFrom(DamageSource.GENERIC, 1);
		}

		if (ticksExisted > MAX_LIFESPAN) {
			attackEntityFrom(DamageSource.GENERIC, 1);
		}

		// Reduce cooldowns
		if (cooldownEgg > 0) {
			cooldownEgg--;
		}
		if (cooldownPollination > 0) {
			cooldownPollination--;
		}
		if (cooldownMate > 0) {
			cooldownMate--;
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (getState().doesMovement && flightTarget != null) {
			double diffX = flightTarget.x + 0.5d - posX;
			double diffY = flightTarget.y + 0.1d - posY;
			double diffZ = flightTarget.z + 0.5d - posZ;

			motionX += (Math.signum(diffX) * 0.5d - motionX) * 0.10000000149011612d;
			motionY += (Math.signum(diffY) * 0.699999988079071d - motionY) * 0.10000000149011612d;
			motionZ += (Math.signum(diffZ) * 0.5d - motionZ) * 0.10000000149011612d;

			float horizontal = (float) (Math.atan2(motionZ, motionX) * 180d / Math.PI) - 90f;
			rotationYaw += MathHelper.wrapDegrees(horizontal - rotationYaw);

			setMoveForward(contained.getGenome().getSpeed());
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
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
	public ItemStack getPickedResult(RayTraceResult target) {
		if (species == null) {
			return ItemStack.EMPTY;
		}
		IButterflyRoot root = species.getRoot();
		IAllele[] template = root.getTemplate(species);
		IButterfly butterfly = root.templateAsIndividual(template);
		return root.getMemberStack(butterfly, EnumFlutterType.BUTTERFLY);
	}

	@Override
	public boolean canMateWith(IEntityButterfly butterfly) {
		if (butterfly.getButterfly().getMate() != null) {
			return false;
		}
		if (getButterfly().getMate() != null) {
			return false;
		}
		return !getButterfly().isGeneticEqual(butterfly.getButterfly());
	}

	@Override
	public boolean canMate() {
		return cooldownMate <= 0;
	}
}

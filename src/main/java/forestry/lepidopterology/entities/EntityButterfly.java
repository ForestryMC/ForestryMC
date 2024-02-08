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
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.api.lepidopterology.genetics.IButterflyRoot;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyHelper;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.root.EmptyRootDefinition;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.utils.AlleleUtils;

//TODO minecraft has flying entities (bat, parrot). Can some of their logic be reused here?
//TODO getMaxSpawnedInChunk?
public class EntityButterfly extends PathfinderMob implements IEntityButterfly {

	private static final String NBT_BUTTERFLY = "BTFLY";
	private static final String NBT_ROOT = "ROT";
	private static final String NBT_POLLEN = "PLN";
	private static final String NBT_STATE = "STATE";
	private static final String NBT_EXHAUSTION = "EXH";
	private static final String NBT_HOME = "HOME";

	/* CONSTANTS */
	public static final int COOLDOWNS = 1500;

	private static final EntityDataAccessor<String> DATAWATCHER_ID_SPECIES = SynchedEntityData.defineId(EntityButterfly.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Integer> DATAWATCHER_ID_SIZE = SynchedEntityData.defineId(EntityButterfly.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Byte> DATAWATCHER_ID_STATE = SynchedEntityData.defineId(EntityButterfly.class, EntityDataSerializers.BYTE);

	private static final float DEFAULT_BUTTERFLY_SIZE = 0.75f;
	private static final EnumButterflyState DEFAULT_STATE = EnumButterflyState.FLYING;

	public static final int EXHAUSTION_REST = 1000;
	public static final int EXHAUSTION_CONSUMPTION = 100 * EXHAUSTION_REST;
	public static final int MAX_LIFESPAN = 24000 * 7; // one minecraft week in ticks

	@Nullable
	private Vec3 flightTarget;
	private int exhaustion;
	private IButterfly contained = ButterflyHelper.getKaryotype().getDefaultTemplate().toIndividual(ButterflyHelper.getRoot());
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
	@OnlyIn(Dist.CLIENT)
	private ResourceLocation textureResource;

	/* CONSTRUCTOR */
	public EntityButterfly(EntityType<EntityButterfly> type, Level world) {
		super(type, world);
		setDefaults();
	}

	//TODO this doesn't play well with registering the entity. So static method for now
	//	public EntityButterfly(EntityType<EntityButterfly> type, World world, IButterfly butterfly, BlockPos homePos) {
	//		super(type, world);
	//		setDefaults();
	//		setIndividual(butterfly);
	//		setHomePosAndDistance(homePos, ModuleLepidopterology.maxDistance);
	//	}

	public static EntityButterfly create(EntityType<EntityButterfly> type, Level world, IButterfly butterfly, BlockPos homePos) {
		EntityButterfly bf = new EntityButterfly(type, world);
		bf.setDefaults();
		bf.setIndividual(butterfly);
		bf.restrictTo(homePos, ModuleLepidopterology.maxDistance);
		return bf;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(DATAWATCHER_ID_SPECIES, "");
		entityData.define(DATAWATCHER_ID_SIZE, (int) (DEFAULT_BUTTERFLY_SIZE * 100));
		entityData.define(DATAWATCHER_ID_STATE, (byte) DEFAULT_STATE.ordinal());
	}

	private void setDefaults() {
		this.goalSelector.addGoal(8, new AIButterflyFlee(this));
		this.goalSelector.addGoal(9, new AIButterflyMate(this));
		this.goalSelector.addGoal(10, new AIButterflyPollinate(this));
		this.goalSelector.addGoal(11, new AIButterflyRest(this));
		this.goalSelector.addGoal(12, new AIButterflyRise(this));
		this.goalSelector.addGoal(12, new AIButterflyWander(this));
	}

	@Override
	public PathfinderMob getEntity() {
		return this;
	}

	/* SAVING & LOADING */
	@Override
	public void addAdditionalSaveData(CompoundTag compoundNBT) {
		super.addAdditionalSaveData(compoundNBT);

		CompoundTag bio = new CompoundTag();
		contained.write(bio);
		compoundNBT.put(NBT_BUTTERFLY, bio);

		if (pollen != null) {
			CompoundTag pln = new CompoundTag();
			pln.putString(NBT_ROOT, pollen.getRoot().getUID());
			pollen.write(pln);
			compoundNBT.put(NBT_POLLEN, pln);
		}

		compoundNBT.putByte(NBT_STATE, (byte) getState().ordinal());
		compoundNBT.putInt(NBT_EXHAUSTION, exhaustion);

		compoundNBT.putLong(NBT_HOME, getRestrictCenter().asLong());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundNBT) {
		super.readAdditionalSaveData(compoundNBT);

		IButterfly butterfly = null;
		if (compoundNBT.contains(NBT_BUTTERFLY)) {
			butterfly = new Butterfly(compoundNBT.getCompound(NBT_BUTTERFLY));
		}
		setIndividual(butterfly);

		if (compoundNBT.contains(NBT_POLLEN)) {
			CompoundTag pollenNBT = compoundNBT.getCompound(NBT_POLLEN);
			IRootDefinition<? super IIndividualRoot<?>> definition = EmptyRootDefinition.empty();
			if (pollenNBT.contains(NBT_ROOT)) {
				definition = GeneticsAPI.apiInstance.getRoot(pollenNBT.getString(NBT_ROOT));
			}
			pollen = definition.orElse(TreeManager.treeRoot).create(pollenNBT);
		}

		EnumButterflyState butterflyState = EnumButterflyState.VALUES[compoundNBT.getByte(NBT_STATE)];
		setState(butterflyState);
		exhaustion = compoundNBT.getInt(NBT_EXHAUSTION);
		BlockPos home = BlockPos.of(compoundNBT.getLong(NBT_HOME));
		restrictTo(home, ModuleLepidopterology.maxDistance);
	}

	public float getWingFlap(float partialTickTime) {
		int offset = species != null ? species.getRegistryName().toString().hashCode() : level.random.nextInt();
		return getState().getWingFlap(this, offset, partialTickTime);
	}

	/* STATE - Used for AI and rendering */
	public void setState(EnumButterflyState state) {
		if (this.state != state) {
			this.state = state;
			if (!level.isClientSide) {
				entityData.set(DATAWATCHER_ID_STATE, (byte) state.ordinal());
			}
		}
	}

	public EnumButterflyState getState() {
		return state;
	}

	public float getSize() {
		return size;
	}

	@Override
	public float getSpeed() {
		return contained.getGenome().getActiveValue(ButterflyChromosomes.SPEED);
	}

	/* DESTINATION */
	@Nullable
	public Vec3 getDestination() {
		return flightTarget;
	}

	public void setDestination(@Nullable Vec3 destination) {
		flightTarget = destination;
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		if (!level.hasChunkAt(pos)) {
			return -100f;
		}

		float weight = 0.0f;
		double distanceToHome = getRestrictCenter().distSqr(pos);

		if (!isWithinHomeDistanceFromPosition(distanceToHome)) {

			weight -= 7.5f + 0.005 * (distanceToHome / 4);
		}

		if (!getButterfly().isAcceptedEnvironment(level, pos.getX(), pos.getY(), pos.getZ())) {
			weight -= 15.0f;
		}

		if (!level.getEntitiesOfClass(EntityButterfly.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).isEmpty()) {
			weight -= 1.0f;
		}

		int depth = getFluidDepth(pos);
		if (depth > 0) {
			weight -= 0.1f * depth;
		} else {
			BlockState blockState = level.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block instanceof FlowerBlock) {
				weight += 2.0f;
			} else if (block instanceof IPlantable) {
				weight += 1.5f;
			} else if (block instanceof BonemealableBlock) {
				weight += 1.0f;
			} else if (blockState.getMaterial() == Material.PLANT) {
				weight += 1.0f;
			}

			BlockPos posBelow = pos.below();
			BlockState blockStateBelow = level.getBlockState(posBelow);
			Block blockBelow = blockStateBelow.getBlock();
			if (blockStateBelow.is(BlockTags.LEAVES)) {
				weight += 2.5f;
			} else if (blockBelow instanceof FenceBlock) {
				weight += 1.0f;
			} else if (blockBelow instanceof WallBlock) {
				weight += 1.0f;
			}
		}

		weight += level.getBrightness(LightLayer.SKY, pos);
		return weight;
	}

	private boolean isWithinHomeDistanceFromPosition(double distanceToHome) {
		return distanceToHome < this.getRestrictRadius() * this.getRestrictRadius();
	}

	private int getFluidDepth(BlockPos pos) {
		ChunkAccess chunk = level.getChunk(pos);
		int xx = pos.getX() & 15;
		int zz = pos.getZ() & 15;
		int depth = 0;
		for (int y = chunk.getHighestSectionPosition() + 15; y > 0; --y) {
			//TODO could be a mutable blockpos if this shows as a hotspot
			BlockState blockState = chunk.getBlockState(new BlockPos(xx, y, zz));
			if (blockState.getMaterial().isLiquid()) {
				depth++;
			} else if (!blockState.isAir()) {
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
		exhaustion = Math.max(exhaustion + change, 0);
	}

	@Override
	public int getExhaustion() {
		return exhaustion;
	}

	/* FLYING ABILITY */
	public boolean canFly() {
		return contained.canTakeFlight(level, getX(), getY(), getZ());
	}

	public void setIndividual(@Nullable IButterfly butterfly) {
		if (butterfly == null) {
			butterfly = ButterflyHelper.getKaryotype().getDefaultTemplate().toIndividual(ButterflyHelper.getRoot());
		}
		contained = butterfly;

		IGenome genome = contained.getGenome();

		//TODO AT methods or more entity types.
		//		isImmuneToFire();
		//		isImmuneToFire = genome.getFireResist();
		size = genome.getActiveValue(ButterflyChromosomes.SIZE);
		//		setSize(size, 0.4f);
		species = genome.getActiveAllele(ButterflyChromosomes.SPECIES);

		if (!level.isClientSide) {
			entityData.set(DATAWATCHER_ID_SIZE, (int) (size * 100));
			entityData.set(DATAWATCHER_ID_SPECIES, species.getRegistryName().toString());
		} else {
			textureResource = species.getEntityTexture();
		}
	}

	@Override
	public IButterfly getButterfly() {
		return contained;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		if (!level.isClientSide) {
			setIndividual(contained);
		}
		return spawnDataIn;
	}

	@Override
	public Component getName() {
		if (species == null) {
			return super.getName();
		}
		return species.getDisplayName();
	}

	@Override
	public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
		return true;
	}

	@Override
	public int getPortalWaitTime() {
		return 1000;
	}

	public boolean isRenderable() {
		return species != null;
	}

	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getTexture() {
		return textureResource;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(Entity other) {
	}

	@Override
	public boolean isNoAi() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return tickCount > MAX_LIFESPAN;
	}

	/* INTERACTION */

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (dead) {
			return InteractionResult.FAIL;
		}
		ItemStack stack = player.getItemInHand(hand);
		if ((stack.getItem() instanceof IToolScoop)) {
			if (!level.isClientSide) {
				IButterflyRoot root = ButterflyHelper.getRoot();
				ILepidopteristTracker tracker = root.getBreedingTracker(level, player.getGameProfile());
				ItemStack itemStack = root.getTypes().createStack(contained.copy(), EnumFlutterType.BUTTERFLY);

				tracker.registerCatch(contained);
				ItemStackUtil.dropItemStackAsEntity(itemStack, level, getX(), getY(), getZ());
				this.dead = true;
			} else {
				player.swing(hand);
			}
			return InteractionResult.SUCCESS;
		}

		return super.mobInteract(player, hand);
	}

	/* LOOT */
	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
		for (ItemStack stack : contained.getLootDrop(this, recentlyHitIn, looting)) {
			ItemStackUtil.dropItemStackAsEntity(stack, level, getX(), getY(), getZ());
		}

		// Drop pollen if any
		IIndividual pollen = getPollen();
		if (pollen != null) {
			IRootDefinition<? extends IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(pollen);
			if (!definition.isPresent()) {
				return;
			}
			definition.ifPresent(root -> {
				ItemStack pollenStack = root.createStack(pollen, EnumGermlingType.POLLEN);
				ItemStackUtil.dropItemStackAsEntity(pollenStack, level, getX(), getY(), getZ());
			});
		}
	}

	/* UPDATING */
	@Override
	public void tick() {
		super.tick();

		// Update stuff client side
		if (level.isClientSide) {
			if (species == null) {
				String speciesUid = entityData.get(DATAWATCHER_ID_SPECIES);
				Optional<IAllele> optionalAllele = AlleleUtils.getAllele(speciesUid);
				if (optionalAllele.isPresent()) {
					IAllele allele = optionalAllele.get();
					if (allele instanceof IAlleleButterflySpecies) {
						species = (IAlleleButterflySpecies) allele;
						textureResource = species.getEntityTexture();
						size = entityData.get(DATAWATCHER_ID_SIZE) / 100f;
					}
				}
			}

			byte stateOrdinal = entityData.get(DATAWATCHER_ID_STATE);
			if (state.ordinal() != stateOrdinal) {
				setState(EnumButterflyState.VALUES[stateOrdinal]);
			}
		}

		Vec3 motion = getDeltaMovement();
		setDeltaMovement(motion.x, motion.y * 0.6000000238418579d, motion.z);

		// Make sure we die if the butterfly hasn't rested in a long, long time.
		if (exhaustion > EXHAUSTION_CONSUMPTION && getRandom().nextInt(20) == 0) {
			hurt(DamageSource.GENERIC, 1);
		}

		if (tickCount > MAX_LIFESPAN) {
			hurt(DamageSource.GENERIC, 1);
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
	protected void customServerAiStep() {
		super.customServerAiStep();

		if (getState().doesMovement && flightTarget != null) {
			double diffX = flightTarget.x + 0.5d - getX();
			double diffY = flightTarget.y + 0.1d - getY();
			double diffZ = flightTarget.z + 0.5d - getZ();

			Vec3 motion = getDeltaMovement();
			double newX = (Math.signum(diffX) * 0.5d - motion.x) * 0.10000000149011612d;
			double newY = (Math.signum(diffY) * 0.699999988079071d - motion.y) * 0.10000000149011612d;
			double newZ = (Math.signum(diffZ) * 0.5d - motion.z) * 0.10000000149011612d;

			setDeltaMovement(newX, newY, newZ);

			float horizontal = (float) (Math.atan2(newZ, newX) * 180d / Math.PI) - 90f;
			setYRot(getYRot() + Mth.wrapDegrees(horizontal - getYRot()));

			setZza(contained.getGenome().getActiveValue(ButterflyChromosomes.SPEED));
		}
	}

	// @Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	@Override
	protected float getSoundVolume() {
		return 0.1F;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		if (species == null) {
			return ItemStack.EMPTY;
		}
		IButterflyRoot root = species.getRoot();
		IAllele[] template = root.getTemplates().getTemplate(species.getRegistryName().toString());
		IButterfly butterfly = root.templateAsIndividual(template);
		return root.getTypes().createStack(butterfly, EnumFlutterType.BUTTERFLY);
	}

	@Override
	public boolean canMateWith(IEntityButterfly butterfly) {
		if (butterfly.getButterfly().getMate().isPresent()) {
			return false;
		}
		if (getButterfly().getMate().isPresent()) {
			return false;
		}
		return !getButterfly().isGeneticEqual(butterfly.getButterfly());
	}

	@Override
	public boolean canMate() {
		return cooldownMate <= 0;
	}
}

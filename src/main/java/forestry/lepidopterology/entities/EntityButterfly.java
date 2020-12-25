/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology.entities;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.api.lepidopterology.genetics.*;
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
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.Optional;

//TODO minecraft has flying entities (bat, parrot). Can some of their logic be reused here?
//TODO getMaxSpawnedInChunk?
public class EntityButterfly extends CreatureEntity implements IEntityButterfly {

    private static final String NBT_BUTTERFLY = "BTFLY";
    private static final String NBT_ROOT = "ROT";
    private static final String NBT_POLLEN = "PLN";
    private static final String NBT_STATE = "STATE";
    private static final String NBT_EXHAUSTION = "EXH";
    private static final String NBT_HOME = "HOME";

    /* CONSTANTS */
    public static final int COOLDOWNS = 1500;

    private static final DataParameter<String> DATAWATCHER_ID_SPECIES = EntityDataManager.createKey(
            EntityButterfly.class,
            DataSerializers.STRING
    );
    private static final DataParameter<Integer> DATAWATCHER_ID_SIZE = EntityDataManager.createKey(
            EntityButterfly.class,
            DataSerializers.VARINT
    );
    private static final DataParameter<Byte> DATAWATCHER_ID_STATE = EntityDataManager.createKey(
            EntityButterfly.class,
            DataSerializers.BYTE
    );

    private static final float DEFAULT_BUTTERFLY_SIZE = 0.75f;
    private static final EnumButterflyState DEFAULT_STATE = EnumButterflyState.FLYING;

    public static final int EXHAUSTION_REST = 1000;
    public static final int EXHAUSTION_CONSUMPTION = 100 * EXHAUSTION_REST;
    public static final int MAX_LIFESPAN = 24000 * 7; // one minecraft week in ticks

    @Nullable
    private Vector3d flightTarget;
    private int exhaustion;
    private IButterfly contained = ButterflyHelper.getKaryotype()
                                                  .getDefaultTemplate()
                                                  .toIndividual(ButterflyHelper.getRoot());
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
    public EntityButterfly(EntityType<EntityButterfly> type, World world) {
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

    public static EntityButterfly create(
            EntityType<EntityButterfly> type,
            World world,
            IButterfly butterfly,
            BlockPos homePos
    ) {
        EntityButterfly bf = new EntityButterfly(type, world);
        bf.setDefaults();
        bf.setIndividual(butterfly);
        bf.setHomePosAndDistance(homePos, ModuleLepidopterology.maxDistance);
        return bf;
    }

    @Override
    protected void registerData() {
        super.registerData();

        dataManager.register(DATAWATCHER_ID_SPECIES, "");
        dataManager.register(DATAWATCHER_ID_SIZE, (int) (DEFAULT_BUTTERFLY_SIZE * 100));
        dataManager.register(DATAWATCHER_ID_STATE, (byte) DEFAULT_STATE.ordinal());
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
    public CreatureEntity getEntity() {
        return this;
    }

    /* SAVING & LOADING */
    @Override
    public void writeAdditional(CompoundNBT compoundNBT) {
        super.writeAdditional(compoundNBT);

        CompoundNBT bio = new CompoundNBT();
        contained.write(bio);
        compoundNBT.put(NBT_BUTTERFLY, bio);

        if (pollen != null) {
            CompoundNBT pln = new CompoundNBT();
            pln.putString(NBT_ROOT, pollen.getRoot().getUID());
            pollen.write(pln);
            compoundNBT.put(NBT_POLLEN, pln);
        }

        compoundNBT.putByte(NBT_STATE, (byte) getState().ordinal());
        compoundNBT.putInt(NBT_EXHAUSTION, exhaustion);

        compoundNBT.putLong(NBT_HOME, getHomePosition().toLong());
    }

    @Override
    public void readAdditional(CompoundNBT compoundNBT) {
        super.readAdditional(compoundNBT);

        IButterfly butterfly = null;
        if (compoundNBT.contains(NBT_BUTTERFLY)) {
            butterfly = new Butterfly(compoundNBT.getCompound(NBT_BUTTERFLY));
        }
        setIndividual(butterfly);

        if (compoundNBT.contains(NBT_POLLEN)) {
            CompoundNBT pollenNBT = compoundNBT.getCompound(NBT_POLLEN);
            IRootDefinition<? super IIndividualRoot<?>> definition = EmptyRootDefinition.empty();
            if (pollenNBT.contains(NBT_ROOT)) {
                definition = GeneticsAPI.apiInstance.getRoot(pollenNBT.getString(NBT_ROOT));
            }
            pollen = definition.orElse(TreeManager.treeRoot).create(pollenNBT);
        }

        EnumButterflyState butterflyState = EnumButterflyState.VALUES[compoundNBT.getByte(NBT_STATE)];
        setState(butterflyState);
        exhaustion = compoundNBT.getInt(NBT_EXHAUSTION);
        BlockPos home = BlockPos.fromLong(compoundNBT.getLong(NBT_HOME));
        setHomePosAndDistance(home, ModuleLepidopterology.maxDistance);
    }

    public float getWingFlap(float partialTickTime) {
        int offset = species != null ? species.getRegistryName().toString().hashCode() : world.rand.nextInt();
        return getState().getWingFlap(this, offset, partialTickTime);
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
        return contained.getGenome().getActiveValue(ButterflyChromosomes.SPEED);
    }

    /* DESTINATION */
    @Nullable
    public Vector3d getDestination() {
        return flightTarget;
    }

    public void setDestination(@Nullable Vector3d destination) {
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

        if (!world.getEntitiesWithinAABB(
                EntityButterfly.class,
                new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)
        ).isEmpty()) {
            weight -= 1.0f;
        }

        int depth = getFluidDepth(pos);
        if (depth > 0) {
            weight -= 0.1f * depth;
        } else {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block instanceof FlowerBlock) {
                weight += 2.0f;
            } else if (block instanceof IPlantable) {
                weight += 1.5f;
            } else if (block instanceof IGrowable) {
                weight += 1.0f;
            } else if (blockState.getMaterial() == Material.PLANTS) {
                weight += 1.0f;
            }

            BlockPos posBelow = pos.down();
            BlockState blockStateBelow = world.getBlockState(posBelow);
            Block blockBelow = blockStateBelow.getBlock();
            if (blockBelow.isIn(BlockTags.LEAVES)) {
                weight += 2.5f;
            } else if (blockBelow instanceof FenceBlock) {
                weight += 1.0f;
            } else if (blockBelow instanceof WallBlock) {
                weight += 1.0f;
            }
        }

        weight += world.getBrightness(pos);
        return weight;
    }

    private boolean isWithinHomeDistanceFromPosition(double distanceToHome) {
        return distanceToHome < this.getMaximumHomeDistance() * this.getMaximumHomeDistance();
    }

    private int getFluidDepth(BlockPos pos) {
        IChunk chunk = world.getChunk(pos);
        int xx = pos.getX() & 15;
        int zz = pos.getZ() & 15;
        int depth = 0;
        for (int y = chunk.getTopFilledSegment() + 15; y > 0; --y) {
            //TODO could be a mutable blockpos if this shows as a hotspot
            BlockState blockState = chunk.getBlockState(new BlockPos(xx, y, zz));
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
        exhaustion = Math.max(exhaustion + change, 0);
    }

    @Override
    public int getExhaustion() {
        return exhaustion;
    }

    /* FLYING ABILITY */
    public boolean canFly() {
        return contained.canTakeFlight(world, getPosX(), getPosY(), getPosZ());
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

        if (!world.isRemote) {
            dataManager.set(DATAWATCHER_ID_SIZE, (int) (size * 100));
            dataManager.set(DATAWATCHER_ID_SPECIES, species.getRegistryName().toString());
        } else {
            textureResource = species.getEntityTexture();
        }
    }

    @Override
    public IButterfly getButterfly() {
        return contained;
    }

    public ILivingEntityData onInitialSpawn(
            IWorld worldIn,
            DifficultyInstance difficultyIn,
            SpawnReason reason,
            @Nullable ILivingEntityData spawnDataIn,
            @Nullable CompoundNBT dataTag
    ) {
        if (!world.isRemote) {
            setIndividual(contained);
        }
        return spawnDataIn;
    }

    @Override
    public ITextComponent getName() {
        if (species == null) {
            return super.getName();
        }
        return species.getDisplayName();
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return true;
    }

    @Override
    public int getMaxInPortalTime() {
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
    public boolean canDespawn(double distanceToClosestPlayer) {
        return ticksExisted > MAX_LIFESPAN;
    }

    /* INTERACTION */

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (dead) {
            return ActionResultType.FAIL;
        }
        ItemStack stack = player.getHeldItem(hand);
        if ((stack.getItem() instanceof IToolScoop)) {
            if (!world.isRemote) {
                IButterflyRoot root = ButterflyHelper.getRoot();
                ILepidopteristTracker tracker = root.getBreedingTracker(world, player.getGameProfile());
                ItemStack itemStack = root.getTypes().createStack(contained.copy(), EnumFlutterType.BUTTERFLY);

                tracker.registerCatch(contained);
                ItemStackUtil.dropItemStackAsEntity(itemStack, world, getPosX(), getPosY(), getPosZ());
                this.dead = true;
            } else {
                player.swingArm(hand);
            }
            return ActionResultType.SUCCESS;
        }

        return super.func_230254_b_(player, hand);
    }

    /* LOOT */
    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        for (ItemStack stack : contained.getLootDrop(this, recentlyHitIn, looting)) {
            ItemStackUtil.dropItemStackAsEntity(stack, world, getPosX(), getPosY(), getPosZ());
        }

        // Drop pollen if any
        IIndividual pollen = getPollen();
        if (pollen != null) {
            IRootDefinition<? extends IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper()
                                                                                                        .getSpeciesRoot(
                                                                                                                pollen);
            if (!definition.isPresent()) {
                return;
            }
            definition.ifPresent(root -> {
                ItemStack pollenStack = root.createStack(pollen, EnumGermlingType.POLLEN);
                ItemStackUtil.dropItemStackAsEntity(pollenStack, world, getPosX(), getPosY(), getPosZ());
            });
        }
    }

    /* UPDATING */
    @Override
    public void tick() {
        super.tick();

        // Update stuff client side
        if (world.isRemote) {
            if (species == null) {
                String speciesUid = dataManager.get(DATAWATCHER_ID_SPECIES);
                Optional<IAllele> optionalAllele = AlleleUtils.getAllele(speciesUid);
                if (optionalAllele.isPresent()) {
                    IAllele allele = optionalAllele.get();
                    if (allele instanceof IAlleleButterflySpecies) {
                        species = (IAlleleButterflySpecies) allele;
                        textureResource = species.getEntityTexture();
                        size = dataManager.get(DATAWATCHER_ID_SIZE) / 100f;
                    }
                }
            }

            byte stateOrdinal = dataManager.get(DATAWATCHER_ID_STATE);
            if (state.ordinal() != stateOrdinal) {
                setState(EnumButterflyState.VALUES[stateOrdinal]);
            }
        }

        Vector3d motion = getMotion();
        setMotion(motion.x, motion.y * 0.6000000238418579d, motion.z);

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
            double diffX = flightTarget.x + 0.5d - getPosX();
            double diffY = flightTarget.y + 0.1d - getPosY();
            double diffZ = flightTarget.z + 0.5d - getPosZ();

            Vector3d motion = getMotion();
            double newX = (Math.signum(diffX) * 0.5d - motion.x) * 0.10000000149011612d;
            double newY = (Math.signum(diffY) * 0.699999988079071d - motion.y) * 0.10000000149011612d;
            double newZ = (Math.signum(diffZ) * 0.5d - motion.z) * 0.10000000149011612d;

            setMotion(newX, newY, newZ);

            float horizontal = (float) (Math.atan2(newZ, newX) * 180d / Math.PI) - 90f;
            rotationYaw += MathHelper.wrapDegrees(horizontal - rotationYaw);

            setMoveForward(contained.getGenome().getActiveValue(ButterflyChromosomes.SPEED));
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
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

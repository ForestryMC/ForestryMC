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
package forestry.apiculture.tiles;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.WorldgenBeekeepingLogic;
import forestry.apiculture.blocks.BlockBeeHive;
import forestry.apiculture.features.ApicultureTiles;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.config.Config;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.packets.PacketActiveUpdate;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

import genetics.api.GeneticHelper;
import genetics.api.individual.IGenome;

public class TileHive extends BlockEntity implements IHiveTile, IActivatable, IBeeHousing {
	private static final DamageSource damageSourceBeeHive = new DamageSourceForestry("bee.hive");

	private final InventoryAdapter contained = new InventoryAdapter(2, "Contained");
	private final HiveBeeHousingInventory inventory;
	private final WorldgenBeekeepingLogic beeLogic;
	private final IErrorLogic errorLogic;
	private final Predicate<LivingEntity> beeTargetPredicate;
	private final TickHelper tickHelper = new TickHelper();

	@Nullable
	private IBee containedBee = null;
	private boolean active = false;
	private boolean angry = false;
	private int calmTime;

	public TileHive(BlockPos pos, BlockState state) {
		super(ApicultureTiles.HIVE.tileType(), pos, state);
		inventory = new HiveBeeHousingInventory(this);
		beeLogic = new WorldgenBeekeepingLogic(this);
		errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
		beeTargetPredicate = new BeeTargetPredicate(this);
	}

	public void tick() {
		if (Config.generateBeehivesDebug) {
			return;
		}
		tickHelper.onTick();

		if (level.isClientSide) {
			if (active && tickHelper.updateOnInterval(4)) {
				if (beeLogic.canDoBeeFX()) {
					beeLogic.doBeeFX();
				}
			}
		} else {
			boolean canWork = beeLogic.canWork(); // must be called every tick to stay updated

			if (tickHelper.updateOnInterval(angry ? 10 : 200)) {
				if (calmTime == 0) {
					if (canWork) {
						if (angry && ModuleApiculture.hiveDamageOnAttack && (level.getLevelData().getDifficulty() != Difficulty.PEACEFUL || ModuleApiculture.hivesDamageOnPeaceful)) {
							AABB boundingBox = AlleleEffect.getBounding(getContainedBee().getGenome(), this);
							List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox, beeTargetPredicate);
							if (!entities.isEmpty()) {
								Collections.shuffle(entities);
								LivingEntity entity = entities.get(0);
								if ((entity instanceof Player || !ModuleApiculture.hivesDamageOnlyPlayers) && (!entity.isInWater() || ModuleApiculture.hivesDamageUnderwater)) {
									attack(entity, 2);
								}
							}
						}
						beeLogic.doWork();
					}
				} else {
					calmTime--;
				}
			}

			setActive(calmTime == 0);
		}
	}

	public IBee getContainedBee() {
		if (this.containedBee == null) {
			IGenome beeGenome = null;
			ItemStack containedBee = contained.getItem(0);
			if (!containedBee.isEmpty()) {
				Optional<IBee> optionalBee = BeeManager.beeRoot.create(containedBee);
				if (optionalBee.isPresent()) {
					IBee bee = optionalBee.get();
					beeGenome = bee.getGenome();
				}
			}
			if (beeGenome == null) {
				beeGenome = getGenomeFromBlock();
			}
			if (beeGenome == null) {
				beeGenome = BeeDefinition.FOREST.getGenome();
			}
			this.containedBee = BeeManager.beeRoot.create(beeGenome);
		}
		return this.containedBee;
	}

	@Nullable
	private IGenome getGenomeFromBlock() {
		if (level.hasChunkAt(worldPosition)) {
			BlockState blockState = level.getBlockState(worldPosition);
			Block block = blockState.getBlock();
			if (block instanceof BlockBeeHive) {
				IHiveRegistry.HiveType hiveType = ((BlockBeeHive) block).getType();
				String speciesUid = hiveType.getSpeciesUid();
				return GeneticHelper.genomeFromTemplate(speciesUid, BeeManager.beeRootDefinition);
			}
		}
		return null;
	}

	public void setContained(List<ItemStack> bees) {
		for (ItemStack itemstack : bees) {
			InventoryUtil.addStack(contained, itemstack, true);
		}
	}

	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		contained.read(compoundNBT);
		beeLogic.read(compoundNBT);
	}


	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		contained.write(compoundNBT);
		beeLogic.write(compoundNBT);
	}

	@Override
	public void calmBees() {
		calmTime = 5;
		angry = false;
		setActive(false);
	}

	@Override
	public boolean isAngry() {
		return angry;
	}

	@Override
	public void onAttack(Level world, BlockPos pos, Player player) {
		if (calmTime == 0) {
			angry = true;
		}
	}

	@Override
	public void onBroken(Level world, BlockPos pos, Player player, boolean canHarvest) {
		if (calmTime == 0) {
			attack(player, 10);
		}

		if (canHarvest) {
			for (ItemStack beeStack : InventoryUtil.getStacks(contained)) {
				if (beeStack != null) {
					ItemStackUtil.dropItemStackAsEntity(beeStack, world, pos);
				}
			}
		}
	}

	private static void attack(LivingEntity entity, int maxDamage) {
		double attackAmount = entity.level.random.nextDouble() / 2.0 + 0.5;
		int damage = (int) (attackAmount * maxDamage);
		if (damage > 0) {
			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, new ResourceLocation(damageSourceBeeHive.msgId), true);
			if (entity.level.random.nextInt(4) >= count) {
				entity.hurt(damageSourceBeeHive, damage);
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		this.active = active;

		if (!level.isClientSide) {
			NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), worldPosition, level);
		}
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}


	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		nbt.putBoolean("active", calmTime == 0);
		beeLogic.write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		setActive(tag.getBoolean("active"));
		beeLogic.read(tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag nbt = pkt.getTag();
		handleUpdateTag(nbt);
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.emptyList();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.emptyList();
	}


	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), getBlockPos());
	}

	@Override
	public EnumHumidity getHumidity() {
		float humidity = getBiome().getDownfall();
		return EnumHumidity.getFromValue(humidity);
	}


	@Override
	public int getBlockLightValue() {
		return getLevel().isDay() ? 15 : 0; // hives may have the sky obstructed but should still be active
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return true; // hives may have the sky obstructed but should still be active
	}

	@Override
	public boolean isRaining() {
		return level.isRainingAt(getBlockPos().above());
	}

	@Override
	public Level getWorldObj() {
		return level;
	}

	@Override
	public Biome getBiome() {
		return getLevel().getBiome(getBlockPos()).value();
	}

	@Override
	@Nullable
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		BlockPos pos = getBlockPos();
		return new Vec3(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getBlockPos();
	}

	private static class BeeTargetPredicate implements Predicate<LivingEntity> {

		private final IHiveTile hive;

		public BeeTargetPredicate(IHiveTile hive) {
			this.hive = hive;
		}

		@Override
		public boolean apply(@Nullable LivingEntity input) {
			if (input != null && input.isAlive() && !input.isInvisible()) {
				if (input instanceof Player) {
					return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(input);
				} else if (hive.isAngry()) {
					return true;
				} else if (input instanceof Enemy) {
					// don't attack semi-passive vanilla mobs
					return !(input instanceof EnderMan) && !(input instanceof ZombifiedPiglin);
				}
			}
			return false;
		}
	}

}

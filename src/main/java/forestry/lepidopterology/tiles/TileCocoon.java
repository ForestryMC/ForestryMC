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
package forestry.lepidopterology.tiles;

import com.google.common.base.Preconditions;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.NetworkUtil;
import forestry.lepidopterology.features.LepidopterologyTiles;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class TileCocoon extends TileEntity implements IStreamable, IOwnedTile, IButterflyCocoon {
    private final OwnerHandler ownerHandler = new OwnerHandler();
    private int age;
    private int maturationTime;
    private IButterfly caterpillar = ButterflyDefinition.CabbageWhite.createIndividual();
    private boolean isSolid;

    public TileCocoon() {
        super(LepidopterologyTiles.COCOON.tileType());
    }

    public TileCocoon(boolean isSolid) {
        super(isSolid ? LepidopterologyTiles.SOLID_COCOON.tileType() : LepidopterologyTiles.COCOON.tileType());
        this.isSolid = isSolid;
        if (isSolid) {
            this.age = 2;
        }
    }

    /* SAVING & LOADING */
    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);

        if (compoundNBT.contains("Caterpillar")) {
            caterpillar = new Butterfly(compoundNBT.getCompound("Caterpillar"));
        }
        ownerHandler.read(compoundNBT);
        age = compoundNBT.getInt("Age");
        maturationTime = compoundNBT.getInt("CATMAT");
        isSolid = compoundNBT.getBoolean("isSolid");
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);

        CompoundNBT subcompound = new CompoundNBT();
        caterpillar.write(subcompound);
        compoundNBT.put("Caterpillar", subcompound);

        ownerHandler.write(compoundNBT);

        compoundNBT.putInt("Age", age);
        compoundNBT.putInt("CATMAT", maturationTime);
        compoundNBT.putBoolean("isSolid", isSolid);
        return compoundNBT;
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        IButterfly caterpillar = getCaterpillar();
        String speciesUID = caterpillar.getIdentifier();
        data.writeString(speciesUID);
        data.writeInt(age);
    }

    @Override
    public void readData(PacketBufferForestry data) {
        String speciesUID = data.readString();
        IButterfly caterpillar = getButterfly(speciesUID);
        setCaterpillar(caterpillar);
        age = data.readInt();
    }

    private static IButterfly getButterfly(String speciesUID) {
        IAllele[] butterflyTemplate = ButterflyManager.butterflyRoot.getTemplates().getTemplate(speciesUID);
        Preconditions.checkNotNull(butterflyTemplate, "Could not find butterfly template for species: %s", speciesUID);
        return ButterflyManager.butterflyRoot.templateAsIndividual(butterflyTemplate);
    }

    @Override
    public IOwnerHandler getOwnerHandler() {
        return ownerHandler;
    }

    //TODO moved to block.onReplaced
    //	@Override
    //	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
    //		return !Block.isEqualTo(oldState.getBlock(), newSate.getBlock());
    //	}

    /* INETWORKEDENTITY */
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        return NBTUtilForestry.writeStreamableToNbt(this, tag);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        int oldAge = age;
        super.handleUpdateTag(state, tag);
        NBTUtilForestry.readStreamableFromNbt(this, tag);
        if (oldAge != age) {
            Minecraft.getInstance().worldRenderer.markForRerender(pos.getX(), pos.getY(), pos.getZ());
            //			world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(getBlockState(), nbt);
    }

    public void onBlockTick() {
        maturationTime++;

        IGenome caterpillarGenome = caterpillar.getGenome();
        int caterpillarMatureTime = Math
                .round((float) caterpillarGenome.getActiveValue(ButterflyChromosomes.LIFESPAN) / (caterpillarGenome.getActiveValue(ButterflyChromosomes.FERTILITY) * 2));

        if (maturationTime >= caterpillarMatureTime) {
            if (age < 2) {
                age++;
                maturationTime = 0;
                BlockState blockState = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, blockState, blockState, 0);
            } else if (caterpillar.canTakeFlight(world, getPos().getX(), getPos().getY(), getPos().getZ())) {
                NonNullList<ItemStack> cocoonDrops = caterpillar.getCocoonDrop(this);
                for (ItemStack drop : cocoonDrops) {
                    ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
                }
                world.setBlockState(getPos(), Blocks.AIR.getDefaultState());
                attemptButterflySpawn(world, caterpillar, getPos());
            }
        }
    }

    private boolean isListEmpty(NonNullList<ItemStack> cocoonDrops) {
        if (cocoonDrops.isEmpty()) {
            return true;
        }
        for (ItemStack stack : cocoonDrops) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
        MobEntity entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(),
                pos.getX(), pos.getY() + 0.1f, pos.getZ());
        Log.trace("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(),
                pos.getZ());
    }

    @Override
    public BlockPos getCoordinates() {
        return getPos();
    }

    @Override
    public IButterfly getCaterpillar() {
        return caterpillar;
    }

    @Override
    public void setCaterpillar(IButterfly butterfly) {
        this.caterpillar = butterfly;
        sendNetworkUpdate();
    }

    private void sendNetworkUpdate() {
        NetworkUtil.sendNetworkPacket(new PacketTileStream(this), pos, world);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public NonNullList<ItemStack> getCocoonDrops() {
        return caterpillar.getCocoonDrop(this);
    }

    @Override
    public boolean isSolid() {
        return isSolid;
    }

}

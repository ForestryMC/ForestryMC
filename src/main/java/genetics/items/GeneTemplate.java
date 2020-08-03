package genetics.items;

import genetics.ApiInstance;
import genetics.Genetics;
import genetics.api.IGeneTemplate;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.root.IIndividualRoot;
import genetics.utils.AlleleUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class GeneTemplate implements IGeneTemplate, ICapabilitySerializable<CompoundNBT> {
    public static final IGeneTemplate EMPTY = new Empty();

    private static final String NBT_ALLELE = "Allele";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_DEFINITION = "Definition";

    private final LazyOptional<IGeneTemplate> holder = LazyOptional.of(() -> this);

    @Nullable
    private IAllele allele;
    @Nullable
    private IChromosomeType type;
    @Nullable
    private IIndividualRoot root;

    @Override
    public Optional<IAllele> getAllele() {
        return Optional.ofNullable(allele);
    }

    @Override
    public Optional<IChromosomeType> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Optional<IIndividualRoot> getRoot() {
        return Optional.ofNullable(root);
    }

    @Override
    public void setAllele(@Nullable IChromosomeType type, @Nullable IAllele allele) {
        this.allele = allele;
        this.type = type;
        if (type != null) {
            root = type.getRoot();
        } else {
            root = null;
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        if (allele != null) {
            compound.putString(NBT_ALLELE, allele.getRegistryName().toString());
        }
        if (type != null && root != null) {
            compound.putByte(NBT_TYPE, (byte) type.getIndex());
            compound.putString(NBT_DEFINITION, root.getUID());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        if (compound.contains(NBT_TYPE) && compound.contains(NBT_DEFINITION)) {
            ApiInstance.INSTANCE.getRoot(compound.getString(NBT_DEFINITION)).ifPresent(def -> {
                this.root = def;
                type = def.getKaryotype().getChromosomeTypes()[compound.getByte(NBT_TYPE)];
            });
        }
        if (compound.contains(NBT_ALLELE)) {
            allele = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE));
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        return Genetics.GENE_TEMPLATE.orEmpty(cap, holder);
    }

    private static class Empty implements IGeneTemplate {
        @Override
        public Optional<IAllele> getAllele() {
            return Optional.empty();
        }

        @Override
        public Optional<IChromosomeType> getType() {
            return Optional.empty();
        }

        @Override
        public Optional<IIndividualRoot> getRoot() {
            return Optional.empty();
        }

        @Override
        public void setAllele(@Nullable IChromosomeType type, @Nullable IAllele allele) {
            //Default Implementation
        }
    }
}

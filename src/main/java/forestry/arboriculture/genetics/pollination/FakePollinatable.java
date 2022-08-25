package forestry.arboriculture.genetics.pollination;

import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import java.util.EnumSet;
import net.minecraftforge.common.EnumPlantType;

public class FakePollinatable implements IPollinatable {

    private final ICheckPollinatable checkPollinatable;

    public FakePollinatable(ICheckPollinatable checkPollinatable) {
        this.checkPollinatable = checkPollinatable;
    }

    @Override
    public EnumSet<EnumPlantType> getPlantType() {
        return checkPollinatable.getPlantType();
    }

    @Override
    public IIndividual getPollen() {
        return checkPollinatable.getPollen();
    }

    @Override
    public boolean canMateWith(IIndividual pollen) {
        return checkPollinatable.canMateWith(pollen);
    }

    @Override
    public void mateWith(IIndividual pollen) {}

    @Override
    public boolean isPollinated() {
        return checkPollinatable.isPollinated();
    }
}

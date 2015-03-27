package forestry.arboriculture.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.gadgets.TileTreeContainer;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;
import forestry.plugins.PluginArboriculture;

public class PacketTreeContainer extends ForestryPacket implements ILocatedPacket {

	private int posX, posY, posZ;
	protected String speciesUID = "";

	public PacketTreeContainer() {

	}

	public PacketTreeContainer(int id, TileTreeContainer treeContainer) {
		super(id);

		posX = treeContainer.xCoord;
		posY = treeContainer.yCoord;
		posZ = treeContainer.zCoord;

		ITree tree = treeContainer.getTree();
		if (tree != null) {
			speciesUID = tree.getIdent();
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeShort(posX);
		data.writeShort(posY);
		data.writeShort(posZ);
		data.writeUTF(speciesUID);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		posX = data.readShort();
		posY = data.readShort();
		posZ = data.readShort();
		speciesUID = data.readUTF();
	}

	@Override
	public TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}

	public boolean matchesTree(ITree tree) {
		if (tree == null) {
			return speciesUID.isEmpty();
		} else {
			return tree.getIdent().equals(speciesUID);
		}
	}

	public ITree getTree() {
		IAllele[] treeTemplate = PluginArboriculture.treeInterface.getTemplate(speciesUID);
		if (treeTemplate != null) {
			return PluginArboriculture.treeInterface.templateAsIndividual(treeTemplate);
		}
		return null;
	}
}

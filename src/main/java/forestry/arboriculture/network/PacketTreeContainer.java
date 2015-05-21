package forestry.arboriculture.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.gadgets.TileTreeContainer;
import forestry.core.network.PacketCoordinates;
import forestry.plugins.PluginArboriculture;

public class PacketTreeContainer extends PacketCoordinates {

	protected String speciesUID = "";

	public PacketTreeContainer() {

	}

	public PacketTreeContainer(int id, TileTreeContainer treeContainer) {
		super(id, treeContainer);

		ITree tree = treeContainer.getTree();
		if (tree != null) {
			speciesUID = tree.getIdent();
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeUTF(speciesUID);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		speciesUID = data.readUTF();
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

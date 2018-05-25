package forestry.api.genetics;

/**
 * Every instance of this enum represents a tab in the database.
 */
public enum EnumDatabaseTab {
	//Used to display information about the active species of the individual.
	ACTIVE_SPECIES,
	//Used to display information about the inactive species of the individual.
	INACTIVE_SPECIES,
	//Used to display information about the products of the active species of the individual.
	PRODUCTS,
	//Used to display information about the mutations of the active species of the individual.
	MUTATIONS
}

package mhfc.net.common.world.area;

import mhfc.net.common.world.controller.IWorldProxy;

public interface IAreaType {
	/**
	 * Called to populate the world that is represented by the
	 * {@link IWorldProxy}.<br>
	 * 
	 * This implies generating the terrain, spawning structures but not
	 * necessarily spawning entities. On the contrary: The entities should not
	 * be spawned until after the Area is being taken into use by a group of
	 * hunters.<br>
	 * Returns the {@link IArea} that is later being used to interact with the
	 * generated instace of this {@link IAreaType}.
	 * 
	 * @param world
	 *            the part of the world that the future Area represents
	 * @return the {@link IArea} controller.
	 */
	IArea populate(IWorldProxy world);

	@Override
	int hashCode();

}

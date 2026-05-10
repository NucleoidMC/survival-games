package supercoder79.survivalgames.game.map.gen.structure;

import net.minecraft.world.entity.ai.behavior.ShufflingList;

public class Structures {
	public static final ShufflingList<StructureGen> POOL = new ShufflingList<StructureGen>()
			.add(OrePileGen.INSTANCE, 2)
			.add(FarmlandStructure.INSTANCE, 2)
			.add(MeteorStructure.INSTANCE, 1)
			.add(SpawnerStructure.INSTANCE, 1);
}

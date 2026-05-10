package supercoder79.survivalgames.game.map.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import supercoder79.survivalgames.game.map.loot.LootHelper;
import supercoder79.survivalgames.game.map.loot.LootProvider;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import xyz.nucleoid.substrate.gen.GenHelper;

public class OrePileGen implements StructureGen {
	public static final StructureGen INSTANCE = new OrePileGen();

	private static final ShufflingList<BlockState> STATES = new ShufflingList<BlockState>()
			.add(Blocks.IRON_ORE.defaultBlockState(), 1)
			.add(Blocks.COAL_ORE.defaultBlockState(), 1);

	@Override
	public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
		pos = pos.below();

		world.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
		world.setBlock(pos.above(), Blocks.LAVA.defaultBlockState(), 3);
		for (Direction direction : GenHelper.HORIZONTALS) {
			world.setBlock(pos.above().relative(direction), Blocks.STONE_BRICKS.defaultBlockState(), 3);
		}

		boolean chestPlaced = false;

		int count = random.nextInt(6) + 4;
		for (int i = 0; i < count; i++) {
			int slX = random.nextInt(12) - random.nextInt(12);
			int slY = random.nextInt(3) - random.nextInt(3);
			int slZ = random.nextInt(12) - random.nextInt(12);
			BlockPos stackLocal = pos.offset(slX, slY, slZ);
			BlockState state = STATES.shuffle().stream().findFirst().get();
			for (int j = 0; j < 40; j++) {
				int aX = random.nextInt(4) - random.nextInt(4);
				int aY = random.nextInt(6) - random.nextInt(6);
				int aZ = random.nextInt(4) - random.nextInt(4);
				BlockPos local = stackLocal.offset(aX, aY, aZ);

				if (world.getBlockState(local.below()).canOcclude() && world.getBlockState(local).isAir() && !world.getBlockState(local.below()).is(Blocks.CHEST)) {
					if (!chestPlaced) {
						chestPlaced = true;

						LootHelper.placeProviderChest(world, local, LootProviders.ORE_PILE);
					} else {
						world.setBlock(local, state, 3);
					}
				}
			}
		}
	}


	@Override
	public int nearbyChestCount(RandomSource random) {
		return random.nextInt(2);
	}

	@Override
	public LootProvider getLootProvider() {
		return LootProviders.ORE_PILE;
	}
}

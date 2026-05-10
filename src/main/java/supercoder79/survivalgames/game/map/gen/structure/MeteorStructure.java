package supercoder79.survivalgames.game.map.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import supercoder79.survivalgames.game.map.loot.LootProvider;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import supercoder79.survivalgames.game.map.loot.WeightedList;

public final class MeteorStructure implements StructureGen {
    public static final MeteorStructure INSTANCE = new MeteorStructure();

    private static final WeightedList<BlockState> STATES = new WeightedList<BlockState>()
            .add(Blocks.OBSIDIAN.defaultBlockState(), 10)
            .add(Blocks.DIAMOND_ORE.defaultBlockState(), 1)
            .add(Blocks.IRON_ORE.defaultBlockState(), 2)
            .add(Blocks.RAW_IRON_BLOCK.defaultBlockState(), 1);

    @Override
    public int nearbyChestCount(RandomSource random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public LootProvider getLootProvider() {
        return LootProviders.METEOR;
    }

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        generateNetherrack(world, pos, random);
        generateMeteor(world, pos, random);
    }

    public void generateNetherrack(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        int radius = 5 + random.nextInt(3);
        double dRadius = radius;
        NormalNoise noise = NormalNoise.create(RandomSource.create(random.nextLong()), -3, 0.1);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double ax = x / dRadius;
                double az = z / dRadius;

                double rad = 1.0 + noise.getValue(pos.getX() + x, pos.getY(), pos.getZ() + z);
                rad += random.nextDouble() * 0.1;

                if (ax * ax + az * az <= rad) {
                    BlockPos top = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos.offset(x, 0, z)).below();
                    world.setBlock(top, Blocks.NETHERRACK.defaultBlockState(), 3);
                    if (random.nextInt(5) == 0) {
                        world.setBlock(top.above(), Blocks.FIRE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    public void generateMeteor(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        int radius = 2 + random.nextInt(2);
        double dRadius = radius;
        NormalNoise noise = NormalNoise.create(RandomSource.create(random.nextLong()), -3, 0.1);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    double ax = x / dRadius;
                    double az = z / dRadius;
                    double ay = y / dRadius;

                    if (ax * ax + az * az + ay * ay <= 1.0 + noise.getValue(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
                        world.setBlock(pos.offset(x, y, z), STATES.pickRandom(random), 3);
                    }
                }
            }
        }
    }
}

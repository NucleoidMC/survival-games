package supercoder79.survivalgames.game.map;

import com.google.common.collect.ImmutableList;
import dev.gegy.noise.sampler.NoiseSampler2d;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import supercoder79.survivalgames.SurvivalGames;
import supercoder79.survivalgames.game.GameTrackable;
import supercoder79.survivalgames.game.GenerationTracker;
import supercoder79.survivalgames.game.config.SurvivalGamesConfig;
import supercoder79.survivalgames.game.map.biome.BiomeGen;
import supercoder79.survivalgames.game.map.biome.generator.BiomeGenerator;
import supercoder79.survivalgames.game.map.biome.source.FakeBiomeSource;
import supercoder79.survivalgames.game.map.gen.structure.ChunkBox;
import supercoder79.survivalgames.game.map.gen.structure.ChunkMask;
import supercoder79.survivalgames.game.map.gen.structure.StructureGen;
import supercoder79.survivalgames.game.map.gen.structure.Structures;
import supercoder79.survivalgames.game.map.loot.LootHelper;
import supercoder79.survivalgames.game.map.loot.LootProviders;
import supercoder79.survivalgames.game.map.noise.NoiseGenerator;
import supercoder79.survivalgames.noise.WorleyNoise;
import supercoder79.survivalgames.noise.simplex.OpenSimplexNoise;
import xyz.nucleoid.substrate.gen.DiskGen;
import xyz.nucleoid.plasmid.api.game.level.generator.GameChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SurvivalGamesChunkGenerator extends GameChunkGenerator {


	private final NoiseSampler2d treeDensityNoise;

	private final WorleyNoise structureNoise;
	private final WorleyNoise chestNoise;

	private final FakeBiomeSource biomeSource;
	private final BiomeGenerator biomeGenerator;
	private final NoiseGenerator noiseGenerator;

	private final Long2ObjectMap<List<PoolElementStructurePiece>> piecesByChunk;
	private final List<SurvivalGamesJigsawGenerator> jigsawGenerator;

	private final BlockState defaultState;
	private final BlockState defaultFluid;
	private final GenerationTracker tracker;

	public SurvivalGamesChunkGenerator(MinecraftServer server, SurvivalGamesConfig config, GenerationTracker tracker) {
		super(new FakeBiomeSource(server.registryAccess().lookupOrThrow(Registries.BIOME), RandomSource.create().nextLong(), config.biomeGenerator()));
		RandomSource random = RandomSource.create();
		this.tracker = tracker;

		this.biomeGenerator = config.biomeGenerator();
		this.noiseGenerator = config.noiseGenerator();
		this.biomeSource = (FakeBiomeSource) this.getBiomeSource();

		this.treeDensityNoise = compile(random, 180.0);

		this.structureNoise = new WorleyNoise(random.nextLong());
		this.chestNoise = new WorleyNoise(random.nextLong());

		this.piecesByChunk = new Long2ObjectOpenHashMap<>();
		this.piecesByChunk.defaultReturnValue(ImmutableList.of());

		List<SurvivalGamesJigsawGenerator> generators = new ArrayList<>();

		this.noiseGenerator.initialize(random, config);

		this.defaultState = config.defaultState();
		this.defaultFluid = config.defaultFluid();

		ChunkMask mask = new ChunkMask();
		ChunkBox townArea = new ChunkBox();

		if (config.townDepth() > 0) {
			SurvivalGamesJigsawGenerator generator = new SurvivalGamesJigsawGenerator(server, this, piecesByChunk);
			generator.arrangePieces(new BlockPos(0, 64, 0), Identifier.fromNamespaceAndPath("survivalgames", "starts"), config.townDepth());
			townArea = generator.getBox();
			generators.add(generator);
			mask.and(townArea);
		}

		for (int i = 0; i < config.outskirtsBuildingCount(); i++) {
			int startX = random.nextInt(config.borderConfig().startSize / 2) - random.nextInt(config.borderConfig().startSize / 2);
			int startZ = random.nextInt(config.borderConfig().startSize / 2) - random.nextInt(config.borderConfig().startSize / 2);

			if (!this.noiseGenerator.shouldOutskirtsSpawn(startX, startZ)) {
				continue;
			}

			BlockPos start = new BlockPos(startX, 0, startZ);
			ChunkPos chunkPos = ChunkPos.containing(start);

			if (mask.isIn(chunkPos)) {
				continue;
			}

			SurvivalGamesJigsawGenerator outskirtGenerator = new SurvivalGamesJigsawGenerator(server, this, piecesByChunk);
			outskirtGenerator.arrangePieces(start, config.outskirtsPool(), 0);

			mask.and(chunkPos);

			generators.add(outskirtGenerator);
		}

		this.jigsawGenerator = generators;

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			DebugJigsawMapper.map(config, this.piecesByChunk, townArea, mask);
		}
	}

	public static NoiseSampler2d compile(RandomSource random, double scale) {
		return SurvivalGames.NOISE_COMPILER.compile(OpenSimplexNoise.create().scale(1 / scale, 1 / scale), NoiseSampler2d.TYPE).create(random.nextLong());
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
		populateNoise(structureAccessor, chunk);
		return CompletableFuture.completedFuture(chunk);
	}

	public void populateNoise(StructureManager structures, ChunkAccess chunk) {
		int chunkX = chunk.getPos().x() * 16;
		int chunkZ = chunk.getPos().z() * 16;

		// To smooth into other chunks, we need to gather all of the nearby structures
		Set<PoolElementStructurePiece> pieces = new ObjectOpenHashSet<>();
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				pieces.addAll(this.piecesByChunk.get(new ChunkPos(chunk.getPos().x() + x, chunk.getPos().z() + z).pack()));
			}
		}

		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		RandomSource random = RandomSource.create();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				double noise = getNoise(x, z);
				BiomeGen biome = this.biomeSource.getRealBiome(x, z);

				int height = (int) (56 + noise);

				// TODO: this is really slow and bad
				for (PoolElementStructurePiece piece : pieces) {
					BoundingBox box = piece.getBoundingBox();
					if (piece.getElement().getProjection() == StructureTemplatePool.Projection.RIGID) {
						// At structure: raise to level
						if (box.intersects(x, z, x, z)) {
							height = Math.max(48, piece.getPosition().getY());
						} else if (box.intersects(x - 8, z - 8, x + 8, z + 8)) {
							// Within radius: smooth
							// I won't lie, I have no idea what I just wrote here.
							// It seems to work though so... it stays.

							double dx = Math.max(0, Math.max(box.minX() - x, x - box.maxX())) / 8.0;
							double dz = Math.max(0, Math.max(box.minZ() - z, z - box.maxZ())) / 8.0;
							double rad = dx * dx + dz * dz;

							double falloff = rad >= 1 ? 0 : (1 - rad) * (1 - rad);

							height = (int) Mth.lerp(falloff, 56 + noise, piece.getPosition().getY());
						}
					}
				}

				// Generation height ensures that the generator iterates up to at least the water level.
				int genHeight = Math.max(height, 48);
				for (int y = 0; y <= genHeight; y++) {
					// Simple surface building
					BlockState state = this.defaultState;
					if (y == height) {
						// If the height and the generation height are the same, it means that we're on land
						if (height == genHeight) {
							state = biome.topState(random, x, z);
						} else {
							// height and genHeight are different, so we're under water. Place dirt instead of grass.
							state = biome.underState(random, x, z);
						}
					} else if ((height - y) <= 3) { //TODO: biome controls under depth
						state = biome.underState(random, x, z);
					} else if (y == 0) {
						state = Blocks.BEDROCK.defaultBlockState();
					}

					// If the y is higher than the land height, then we must place water
					if (y > height) {
						state = defaultFluid;
					}

					// Set the state here
					chunk.setBlockState(mutable.set(x, y, z), state);
				}
			}
		}
	}

	private double getNoise(int x, int z) {
		return this.noiseGenerator.getHeightAt(this.biomeSource, x, z);
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
		int height = (int) (56 + getNoise(x, z));
		return Math.max(height, 50);
	}

	public void generateJigsaws(WorldGenLevel region, ChunkAccess chunk, StructureManager structures) {
		ChunkPos chunkPos = chunk.getPos();
		List<PoolElementStructurePiece> pieces = this.piecesByChunk.get(chunkPos.pack());

		if (pieces != null) {
			// generate all intersecting pieces with the mask of this chunk
			BoundingBox chunkMask = new BoundingBox(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 255, chunkPos.getMaxBlockZ());
			for (PoolElementStructurePiece piece : pieces) {
				piece.place(region, structures, this, RandomSource.create(), chunkMask, BlockPos.ZERO, false);
			}
		}
	}

	@Override
	public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structures) {
		generateJigsaws(world, chunk, structures);

		int chunkX = chunk.getPos().x() * 16;
		int chunkZ = chunk.getPos().z() * 16;
		RandomSource random = RandomSource.create();

		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		boolean spawnedStructure = false;

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				BiomeGen biome = this.biomeSource.getRealBiome(x, z);

				int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);

				if (y > 48) {
					if (this.chestNoise.sample(x / 45.0, z / 45.0) < 0.01) {
						LootHelper.placeProviderChest(world, mutable.set(x, y, z).immutable(), LootProviders.GENERIC);
					}

					if (!spawnedStructure && this.structureNoise.sample(x / 120.0, z / 120.0) < 0.005) {
						spawnedStructure = true;

						StructureGen structure = Structures.POOL.shuffle().stream().findFirst().get();
						structure.generate(world, mutable.set(x, y, z).immutable(), random);

						for (int i = 0; i < structure.nearbyChestCount(random); i++) {
							int ax = x + (random.nextInt(16) - random.nextInt(16));
							int az = z + (random.nextInt(16) - random.nextInt(16));
							int ay = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ax, az);

							LootHelper.placeProviderChest(world, mutable.set(ax, ay, az).immutable(), structure.getLootProvider());
						}

						if (structure instanceof GameTrackable trackable) {
							trackable.getTracker().track(this.tracker, mutable.set(x, y, z).immutable());
						}
					}

					y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);

					int treeDensity = (int) biome.modifyTreeChance((treeDensityNoise.get(x, z) + 1) * 64);

					if (random.nextInt(96 + treeDensity) == 0) {
						biome.tree(x, z, random).generate(world, mutable.set(x, y, z).immutable(), random);
					}

					if (random.nextInt(biome.grassChance(x, z, random)) == 0) {
						biome.grass(x, z, random).generate(world, mutable.set(x, y, z).immutable(), random);
					}
				} else {
					if (!this.biomeGenerator.generateSnow()) {
						if (random.nextInt(384) == 0) {
							generateBoats(world, random, new BlockPos(x, world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z));
						}
					}

					if (random.nextInt(64) == 0) {
						DiskGen.INSTANCE.generate(world, mutable.set(x, y, z).immutable(), random);
					}
				}
			}
		}

		if (this.biomeGenerator.generateSnow()) {
			mutable = new BlockPos.MutableBlockPos();
			for (int x = chunkX; x < chunkX + 16; x++) {
				for (int z = chunkZ; z < chunkZ + 16; z++) {
					int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
					mutable.set(x, y, z);

					if (world.isEmptyBlock(mutable)) {
						BlockState down = world.getBlockState(mutable.below());
						if (down.isSolidRender() || down.is(BlockTags.LEAVES)) {
							world.setBlock(mutable, Blocks.SNOW.defaultBlockState(), 3);

							if (down.hasProperty(BlockStateProperties.SNOWY)) {
								world.setBlock(mutable.below(), down.setValue(BlockStateProperties.SNOWY, true), 3);
							}
						} else if (down.getFluidState().is(FluidTags.WATER)) {
							world.setBlock(mutable.below(), Blocks.ICE.defaultBlockState(), 3);
						}
					}
				}
			}
		}
	}

	private static void generateBoats(WorldGenLevel world, RandomSource random, BlockPos pos) {
		int count = 0;

		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				if (world.getBlockState(new BlockPos(pos.getX() + x, world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX() + x, pos.getZ() + z), pos.getZ() + z).below()).is(Blocks.WATER)) {
					count++;
				}
			}
		}

		if (count > 16 && count < 38) {
			for (int x = -3; x <= 3; x++) {
				for (int z = -3; z <= 3; z++) {
					if (x * x + z * z < 3 * 3 + random.nextInt(3)) {
						BlockPos local = new BlockPos(pos.getX() + x, world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX() + x, pos.getZ() + z), pos.getZ() + z).below();

						if (world.getBlockState(local).is(Blocks.WATER)) {
							world.setBlock(local, Blocks.OAK_PLANKS.defaultBlockState(), 3);
						}
					}
				}
			}

			int boatCount = 6 + random.nextInt(8);
			int placed = 0;
			for (int i = 0; i < boatCount; i++) {
				int dx = random.nextInt(5) - random.nextInt(5);
				int dz = random.nextInt(5) - random.nextInt(5);

				BlockPos local = new BlockPos(pos.getX() + dx, world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX() + dx, pos.getZ() + dz), pos.getZ() + dz).below();

				if (world.getBlockState(local).is(Blocks.WATER)) {
					placed++;
					Boat boat = EntityTypes.OAK_BOAT.create(world.getLevel(), EntitySpawnReason.CHUNK_GENERATION);
					boat.setPosRaw(local.getX(), local.getY() + 1, local.getZ());
					world.addFreshEntity(boat);

					if (placed >= 4) {
						return;
					}
				}
			}
		}
	}

	@Override
	public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager world, StructureManager structureAccessor, ChunkAccess chunk) {
	}
}
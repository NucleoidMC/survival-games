package supercoder79.survivalgames.game.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import supercoder79.survivalgames.game.config.Y256Height;
import supercoder79.survivalgames.game.map.gen.structure.ChunkBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.ArrayList;
import java.util.List;

public final class SurvivalGamesJigsawGenerator {
    private final RandomSource random = RandomSource.create();
    private final RegistryAccess registryManager;
    private final StructureTemplateManager structureManager;
    private final ChunkGenerator generator;

    private BlockPos origin = BlockPos.ZERO;
    private final Long2ObjectMap<List<PoolElementStructurePiece>> piecesByChunk;
    private final ChunkBox box;

    public SurvivalGamesJigsawGenerator(MinecraftServer server, ChunkGenerator generator, Long2ObjectMap<List<PoolElementStructurePiece>> piecesByChunk) {
        this.registryManager = server.registryAccess();
        this.structureManager = server.getStructureManager();
        this.generator = generator;
        this.piecesByChunk = piecesByChunk;
        this.box = new ChunkBox();
    }

    public void arrangePieces(BlockPos origin, Identifier startPoolId, int depth) {
        this.origin = origin;

        List<PoolElementStructurePiece> pieces = this.generateArrangedPieces(origin, startPoolId, depth);
        this.associatePiecesByChunk(pieces);

        for (Long pos : this.piecesByChunk.keySet()) {
            this.box.encompass(ChunkPos.unpack(pos));
        }
    }

    private List<PoolElementStructurePiece> generateArrangedPieces(BlockPos origin, Identifier startPoolId, int depth) {
        List<PoolElementStructurePiece> pieces = new ArrayList<>();

        // we start with a starting piece which all further pieces will branch off from
        PoolElementStructurePiece startPiece = this.createStartPiece(origin, startPoolId);
         this.placePieceOnGround(origin, startPiece);
        // Not quite sure how to port this

        pieces.add(startPiece);

        // [Patbox]: I wasn't sure how to get to correctly update this, but looking that this class doesn't seem to be actually used, I commented it out

        // invoke vanilla code to handle the actual arrangement logic
        //StructurePoolBasedGenerator.method_27230(this.registryManager, startPiece, depth, PoolStructurePiece::new, this.generator, this.structureManager, pieces, this.random, Y256Height.INSTANCE);

        return pieces;
    }

    private PoolElementStructurePiece createStartPiece(BlockPos origin, Identifier startPoolId) {
        StructureTemplatePool pool = this.registryManager.lookupOrThrow(Registries.TEMPLATE_POOL).getValue(startPoolId);
        if (pool == null) {
            //throw new IllegalStateException("missing start pool: '" + startPoolId + "'");
        }

        Rotation rotation = Rotation.getRandom(this.random);
        StructurePoolElement element = pool.getRandomTemplate(this.random);

        return new PoolElementStructurePiece(
                this.structureManager, element,
                origin, element.getGroundLevelDelta(), rotation,
                element.getBoundingBox(this.structureManager, origin, rotation),
                LiquidSettings.APPLY_WATERLOGGING
        );
    }

    private void placePieceOnGround(BlockPos origin, PoolElementStructurePiece piece) {
        BoundingBox box = piece.getBoundingBox();
        int centerX = (box.maxX() + box.maxX()) / 2;
        int centerZ = (box.maxZ() + box.minZ()) / 2;
        int centerY = origin.getY() + this.generator.getFirstFreeHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, Y256Height.INSTANCE, null);

        // offset the piece to be level with the ground at its center
        int targetY = box.minY() + piece.getGroundLevelDelta();
        piece.move(0, centerY - targetY, 0);
    }

    private void associatePiecesByChunk(List<PoolElementStructurePiece> pieces) {
        for (PoolElementStructurePiece piece : pieces) {
            BoundingBox box = piece.getBoundingBox();
            int minChunkX = box.minX() >> 4;
            int minChunkZ = box.minZ() >> 4;
            int maxChunkX = box.maxX() >> 4;
            int maxChunkZ = box.maxZ() >> 4;

            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                    long chunkPos = ChunkPos.pack(chunkX, chunkZ);
                    List<PoolElementStructurePiece> piecesByChunk = this.piecesByChunk.computeIfAbsent(chunkPos, p -> new ArrayList<>());
                    piecesByChunk.add(piece);
                }
            }
        }
    }

    public void generate(WorldGenRegion region, StructureManager structures) {
        ChunkPos chunkPos = new ChunkPos(region.getCenter().x(), region.getCenter().x());
        List<PoolElementStructurePiece> pieces = this.piecesByChunk.get(chunkPos.pack());

        if (pieces != null) {
            // generate all intersecting pieces with the mask of this chunk
            BoundingBox chunkMask = new BoundingBox(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 255, chunkPos.getMaxBlockZ());
            for (PoolElementStructurePiece piece : pieces) {
                piece.place(region, structures, this.generator, this.random, chunkMask, this.origin, false);
            }
        }
    }

    public List<PoolElementStructurePiece> getPiecesInChunk(ChunkPos pos) {
        return this.piecesByChunk.get(pos.pack());
    }

    public ChunkBox getBox() {
        return box;
    }
}

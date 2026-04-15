package com.capgemini.hms.room.controller.api;

import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.room.dto.BlockDTO;
import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blocks")
@Tag(name = "Infrastructure: Blocks", description = "Endpoints for managing hospital blocks and floors")
/**
 * REST controller exposing endpoints for hospital block management.
 * Responsible for request validation and response mapping for block-related
 * operations (list, create, delete). Delegates persistence and domain
 * operations to `BlockService` and maps between `Block` and `BlockDTO`.
 * No domain business rules are implemented here.
 */
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping
    @Operation(summary = "Get all hospital blocks", description = "Returns a list of all defined blocks (floor + building code)")
    public ResponseEntity<ApiResponse<List<BlockDTO>>> getAllBlocks() {
        List<BlockDTO> blocks = blockService.getAllBlocks().stream()
                .map(b -> new BlockDTO(b.getId().getBlockFloor(), b.getId().getBlockCode()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new block", description = "Adds a structural block to the hospital infrastructure")
    public ResponseEntity<ApiResponse<BlockDTO>> createBlock(@Valid @RequestBody BlockDTO blockDTO) {
        Block block = new Block(new BlockId(blockDTO.getBlockFloor(), blockDTO.getBlockCode()));
        Block saved = blockService.saveBlock(block);
        return ResponseEntity.ok(ApiResponse.success(
                new BlockDTO(saved.getId().getBlockFloor(), saved.getId().getBlockCode()),
                "Block created successfully"
        ));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove a block", description = "Deletes a block record. Note: This may fail if rooms are still assigned to this block.")
    public ResponseEntity<ApiResponse<String>> deleteBlock(@RequestParam Integer floor, @RequestParam Integer code) {
        blockService.deleteBlock(floor, code);
        return ResponseEntity.ok(ApiResponse.success("Block deleted successfully"));
    }
}

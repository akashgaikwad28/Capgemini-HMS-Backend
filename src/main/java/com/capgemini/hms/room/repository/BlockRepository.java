package com.capgemini.hms.room.repository;

import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository abstraction for `Block` persistence operations. Extends
 * `JpaRepository` to provide CRUD and query capabilities for the composite
 * primary key `BlockId`.
 */
public interface BlockRepository extends JpaRepository<Block, BlockId> {
}

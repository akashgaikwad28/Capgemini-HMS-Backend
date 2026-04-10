package com.capgemini.hms.medication.repository;

import com.capgemini.hms.medication.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {
    @Query("SELECT m FROM Medication m WHERE m.isDeleted = false")
    Page<Medication> findAllActive(Pageable pageable);

    @Query("SELECT m FROM Medication m WHERE m.isDeleted = false AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Medication> searchActive(@Param("query") String query, Pageable pageable);
}

package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Chord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChordRepository extends JpaRepository<Chord, Long> {
    List<Chord> findByNameStartingWithIgnoreCase(String name);

    @Query("""
        SELECT c FROM Chord c
        JOIN c.notes n
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:notes IS NULL OR n.name IN :notes)
        GROUP BY c
        HAVING COUNT(DISTINCT n.id) = :noteCount
    """)
    List<Chord> findByNameAndNotes(@Param("name") String name,
                                   @Param("notes") List<String> notes,
                                   @Param("noteCount") long noteCount);
}

package com.oryanend.tom_perfeito_api.repositories;

import com.oryanend.tom_perfeito_api.entities.Chord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChordRepository extends JpaRepository<Chord, Long> {
  List<Chord> findByNameStartingWithIgnoreCase(String name);

  @Query("""
SELECT c FROM Chord c
JOIN c.notes n
WHERE (:name IS NULL OR c.name LIKE CONCAT(CAST(:name as string), '%'))
AND (
    :notes IS NULL OR
    CONCAT(
        n.name,
        CASE
            WHEN n.accidental = 'SHARP' THEN '#'
            WHEN n.accidental = 'FLAT' THEN 'b'
            ELSE ''
        END
    ) IN :notes
)
GROUP BY c
HAVING COUNT(DISTINCT n.id) = :noteCount
""")
  List<Chord> findByNameAndNotes(
      @Param("name") String name,
      @Param("notes") List<String> notes,
      @Param("noteCount") long noteCount);
}

package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.ConstituencyResultEntity;
import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.repository.ConstituencyRepository;
import nl.hva.election_backend.repository.ConstituencyResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConstituencyServiceTest {

    @Mock
    private ConstituencyRepository constituencyRepository;

    @Mock
    private ConstituencyResultRepository resultRepository;

    @InjectMocks
    private ConstituencyService constituencyService;

    // --- Tests for saveIfNotExists ---

    @Test
    void saveIfNotExists_WhenExists_ReturnsNull() {
        // Arrange
        String id = "C1";
        int year = 2023;
        String name = "Amsterdam";

        // Simulate that the DB already finds a match
        when(constituencyRepository.findByNameAndYear(name, year))
                .thenReturn(Optional.of(new ConstituencyEntity(id, year, name)));

        // Act
        ConstituencyEntity result = constituencyService.saveIfNotExists(id, year, name);

        // Assert
        assertNull(result);
        // Verify we never attempted to save
        verify(constituencyRepository, never()).save(any(ConstituencyEntity.class));
    }

    @Test
    void saveIfNotExists_WhenNew_SavesAndReturnsEntity() {
        // Arrange
        String id = "C1";
        int year = 2023;
        String name = "Amsterdam";
        ConstituencyEntity expectedEntity = new ConstituencyEntity(id, year, name);

        when(constituencyRepository.findByNameAndYear(name, year)).thenReturn(Optional.empty());
        when(constituencyRepository.save(any(ConstituencyEntity.class))).thenReturn(expectedEntity);

        // Act
        ConstituencyEntity result = constituencyService.saveIfNotExists(id, year, name);

        // Assert
        assertNotNull(result);
        assertEquals("Amsterdam", result.getName());
        verify(constituencyRepository).save(any(ConstituencyEntity.class));
    }

    // --- Tests for saveResultIfNotExists ---

    @Test
    void saveResultIfNotExists_WhenExists_ReturnsNull() {
        // Arrange
        int year = 2023;
        String cId = "C1";
        String pId = "P1";
        int votes = 100;

        when(resultRepository.existsByYearAndConstituencyIdAndPartyId(year, cId, pId))
                .thenReturn(true);

        // Act
        ConstituencyResultEntity result = constituencyService.saveResultIfNotExists(year, cId, pId, votes);

        // Assert
        assertNull(result);
        verify(resultRepository, never()).save(any(ConstituencyResultEntity.class));
    }

    @Test
    void saveResultIfNotExists_WhenNew_SavesAndReturnsEntity() {
        // Arrange
        int year = 2023;
        String cId = "C1";
        String pId = "P1";
        int votes = 100;
        ConstituencyResultEntity expected = new ConstituencyResultEntity(year, cId, pId, votes);

        when(resultRepository.existsByYearAndConstituencyIdAndPartyId(year, cId, pId))
                .thenReturn(false);
        when(resultRepository.save(any(ConstituencyResultEntity.class))).thenReturn(expected);

        // Act
        ConstituencyResultEntity result = constituencyService.saveResultIfNotExists(year, cId, pId, votes);

        // Assert
        assertNotNull(result);
        assertEquals(votes, result.getValidVotes());
        verify(resultRepository).save(any(ConstituencyResultEntity.class));
    }

    // --- Tests for getConstituencies (including toConstituency mapping) ---

    @Test
    void getConstituencies_MapsEntitiesToDTOsCorrectly() {
        // Arrange
        int electionId = 2023;

        // Create Party Entity
        PartyEntity partyEntity = new PartyEntity();
        partyEntity.setPartyId("1");
        partyEntity.setName("Party A");

        // Create Result Entity and link it to Party
        ConstituencyResultEntity resultEntity = new ConstituencyResultEntity();
        resultEntity.setValidVotes(500);
        resultEntity.setParty(partyEntity);

        // Create Constituency Entity and link it to Result
        ConstituencyEntity constEntity = new ConstituencyEntity("C1", electionId, "Utrecht");
        Set<ConstituencyResultEntity> results = new HashSet<>();
        results.add(resultEntity);
        constEntity.setResults(results);

        when(constituencyRepository.findAllByYear(electionId)).thenReturn(Set.of(constEntity));

        // Act
        Set<Constituency> resultDTOs = constituencyService.getConstituencies(electionId);

        // Assert
        assertNotNull(resultDTOs);
        assertEquals(1, resultDTOs.size());

        Constituency dto = resultDTOs.iterator().next();
        assertEquals("Utrecht", dto.getName());
        assertEquals("C1", dto.getConstituencyId());

        // Check nested Party mapping
        assertEquals(1, dto.getParties().size());
        Party partyModel = dto.getParties().iterator().next();
        assertEquals("Party A", partyModel.getName());
        assertEquals(500, partyModel.getVotes());
    }

    @Test
    void getConstituencies_NoData_ReturnsEmptySet() {
        // Arrange
        int electionId = 1990;
        when(constituencyRepository.findAllByYear(electionId)).thenReturn(Set.of());

        // Act
        Set<Constituency> result = constituencyService.getConstituencies(electionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
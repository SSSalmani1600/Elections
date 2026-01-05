package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.MunicipalityDto;
import nl.hva.election_backend.dto.PartyResultDto;
import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.MunicipalityResultEntity;
import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.repository.MunicipalityRepository;
import nl.hva.election_backend.repository.MunicipalityResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MunicipalityServiceTest {

    @Mock
    private MunicipalityRepository municipalityRepo;

    @Mock
    private MunicipalityResultRepository resultRepo;

    @InjectMocks
    private MunicipalityService municipalityService;

    // --- Tests for saveIfNotExists ---

    @Test
    void saveIfNotExists_WhenExists_ReturnsNull() {
        // Arrange
        int year = 2023;
        String muniId = "M001";

        when(municipalityRepo.existsByYearAndMunicipalityId(year, muniId)).thenReturn(true);

        // Act
        MunicipalityEntity result = municipalityService.saveIfNotExists(year, muniId, "C001", "Amsterdam");

        // Assert
        assertNull(result);
        verify(municipalityRepo, never()).save(any(MunicipalityEntity.class));
    }

    @Test
    void saveIfNotExists_WhenNew_SavesAndReturnsEntity() {
        // Arrange
        int year = 2023;
        String muniId = "M001";
        String constId = "C001";
        String name = "Amsterdam";
        MunicipalityEntity expected = new MunicipalityEntity(year, muniId, constId, name);

        when(municipalityRepo.existsByYearAndMunicipalityId(year, muniId)).thenReturn(false);
        when(municipalityRepo.save(any(MunicipalityEntity.class))).thenReturn(expected);

        // Act
        MunicipalityEntity result = municipalityService.saveIfNotExists(year, muniId, constId, name);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(municipalityRepo).save(any(MunicipalityEntity.class));
    }

    // --- Tests for saveResultIfNotExists ---

    @Test
    void saveResultIfNotExists_WhenExists_ReturnsNull() {
        // Arrange
        when(resultRepo.existsByYearAndMunicipalityIdAndPartyId(anyInt(), anyString(), anyString()))
                .thenReturn(true);

        // Act
        MunicipalityResultEntity result = municipalityService.saveResultIfNotExists(2023, "M001", "P001", 100);

        // Assert
        assertNull(result);
        verify(resultRepo, never()).save(any(MunicipalityResultEntity.class));
    }

    @Test
    void saveResultIfNotExists_WhenNew_SavesAndReturnsEntity() {
        // Arrange
        int year = 2023;
        String muniId = "M001";
        String partyId = "P001";
        int votes = 500;
        MunicipalityResultEntity expected = new MunicipalityResultEntity(year, muniId, partyId, votes);

        when(resultRepo.existsByYearAndMunicipalityIdAndPartyId(year, muniId, partyId)).thenReturn(false);
        when(resultRepo.save(any(MunicipalityResultEntity.class))).thenReturn(expected);

        // Act
        MunicipalityResultEntity result = municipalityService.saveResultIfNotExists(year, muniId, partyId, votes);

        // Assert
        assertNotNull(result);
        assertEquals(votes, result.getValidVotes());
        verify(resultRepo).save(any(MunicipalityResultEntity.class));
    }

    // --- Tests for getMunicipalityData ---

    @Test
    void getMunicipalityData_Success_ReturnsMappedDto() {
        // Arrange
        int year = 2023;
        String name = "Rotterdam";

        // Create Party Entity
        PartyEntity party = new PartyEntity();
        party.setPartyId("10");
        party.setName("Test Party");

        // Create Result Entity and link to Party
        MunicipalityResultEntity resultEntity = new MunicipalityResultEntity();
        resultEntity.setPartyId("10"); // String ID in result
        resultEntity.setValidVotes(1234);
        resultEntity.setParty(party); // Link objects

        // Create Municipality Entity and link to Results
        MunicipalityEntity entity = new MunicipalityEntity(year, "M002", "C002", name);
        entity.setResults(List.of(resultEntity));

        when(municipalityRepo.findWithResultsByYearsAndName(year, name))
                .thenReturn(Optional.of(entity));

        // Act
        MunicipalityDto dto = municipalityService.getMunicipalityData(year, name);

        // Assert
        assertNotNull(dto);
        assertEquals("M002", dto.getMunicipalityId());
        assertEquals(name, dto.getName());

        // Validate nested Party/Result list
        assertNotNull(dto.getParties());
        assertEquals(1, dto.getParties().size());

        PartyResultDto partyDto = dto.getParties().getFirst();
        assertEquals("Test Party", partyDto.getName());
        assertEquals(1234, partyDto.getVotes());
        // Verify dependency interaction
        verify(municipalityRepo).findWithResultsByYearsAndName(year, name);
    }

    @Test
    void getMunicipalityData_NotFound_ThrowsException() {
        // Arrange
        int year = 2023;
        String name = "Unknown City";

        when(municipalityRepo.findWithResultsByYearsAndName(year, name))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            municipalityService.getMunicipalityData(year, name);
        });

        assertEquals("Municipality not found", exception.getMessage());
    }
}
package ru.bauman.seminar.constellation.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.service.entity.impl.ConstellationEntityServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты для ConstellationEntityService")
public class ConstellationRepositoryMockTest {

	@Mock
	private ConstellationRepository constellationRepository;

	@InjectMocks
	private ConstellationEntityServiceImpl constellationEntityService;

	private final String CONST_NAME = "Test Constellation";
	private final String CONST_DESC = "Description";
	private Constellation testConstellation;

	@BeforeEach
	void setUp() {
		// Создаём объект без ID, затем устанавливаем ID через сеттер
		testConstellation = Constellation.builder()
				.name(CONST_NAME)
				.description(CONST_DESC)
				.build();
		testConstellation.setId(1L);
	}

	@Test
	@DisplayName("findById должен возвращать группировку, если она существует")
	void findById_ExistingId_ReturnsConstellation() {
		when(constellationRepository.findById(1L)).thenReturn(Optional.of(testConstellation));

		Constellation result = constellationEntityService.findById(1L);

		assertNotNull(result);
		assertEquals(CONST_NAME, result.getName());
		verify(constellationRepository).findById(1L);
	}

	@Test
	@DisplayName("findById должен бросать исключение, если группировка не найдена")
	void findById_NonExistingId_ThrowsEntityNotFoundException() {
		when(constellationRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> constellationEntityService.findById(99L));
		verify(constellationRepository).findById(99L);
	}

	@Test
	@DisplayName("findByName должен возвращать группировку, если она существует")
	void findByName_ExistingName_ReturnsConstellation() {
		when(constellationRepository.findByName(CONST_NAME)).thenReturn(Optional.of(testConstellation));

		Constellation result = constellationEntityService.findByName(CONST_NAME);

		assertNotNull(result);
		assertEquals(CONST_NAME, result.getName());
		verify(constellationRepository).findByName(CONST_NAME);
	}

	@Test
	@DisplayName("findByName должен бросать исключение, если группировка не найдена")
	void findByName_NonExistingName_ThrowsEntityNotFoundException() {
		when(constellationRepository.findByName("unknown")).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> constellationEntityService.findByName("unknown"));
		verify(constellationRepository).findByName("unknown");
	}

	@Test
	@DisplayName("findAll должен возвращать список всех группировок")
	void findAll_ReturnsAllConstellations() {
		List<Constellation> constellations = List.of(testConstellation);
		when(constellationRepository.findAll()).thenReturn(constellations);

		List<Constellation> result = constellationEntityService.findAll();

		assertEquals(1, result.size());
		assertEquals(CONST_NAME, result.get(0).getName());
		verify(constellationRepository).findAll();
	}

	@Test
	@DisplayName("save должен создавать новую группировку, если имя уникально")
	void save_NewConstellation_Success() {
		Constellation newConstellation = Constellation.builder()
				.name(CONST_NAME)
				.description(CONST_DESC)
				.build();

		when(constellationRepository.existsByName(CONST_NAME)).thenReturn(false);
		when(constellationRepository.save(newConstellation)).thenAnswer(invocation -> {
			Constellation saved = invocation.getArgument(0);
			saved.setId(1L);
			return saved;
		});

		Constellation saved = constellationEntityService.save(newConstellation);

		assertNotNull(saved);
		assertEquals(1L, saved.getId());
		assertEquals(CONST_NAME, saved.getName());
		verify(constellationRepository).existsByName(CONST_NAME);
		verify(constellationRepository).save(newConstellation);
	}

	@Test
	@DisplayName("save должен бросать исключение при попытке создать дубликат имени")
	void save_DuplicateName_ThrowsIllegalArgumentException() {
		Constellation newConstellation = Constellation.builder()
				.name(CONST_NAME)
				.build();

		when(constellationRepository.existsByName(CONST_NAME)).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> constellationEntityService.save(newConstellation));
		verify(constellationRepository, never()).save(any());
	}

	@Test
	@DisplayName("save должен обновлять существующую группировку при смене имени на уникальное")
	void save_UpdateConstellation_Success() {
		Constellation existing = Constellation.builder()
				.name("OldName")
				.description("OldDesc")
				.build();
		existing.setId(1L);

		Constellation updated = Constellation.builder()
				.name("NewName")
				.description("NewDesc")
				.build();
		updated.setId(1L);

		when(constellationRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(constellationRepository.existsByName("NewName")).thenReturn(false);
		when(constellationRepository.save(updated)).thenReturn(updated);

		Constellation result = constellationEntityService.save(updated);

		assertNotNull(result);
		assertEquals("NewName", result.getName());
		verify(constellationRepository).findById(1L);
		verify(constellationRepository).existsByName("NewName");
		verify(constellationRepository).save(updated);
	}

	@Test
	@DisplayName("save должен бросать исключение при обновлении, если новое имя уже занято другой группировкой")
	void save_UpdateWithDuplicateName_ThrowsIllegalArgumentException() {
		Constellation existing = Constellation.builder()
				.name("OldName")
				.build();
		existing.setId(1L);

		Constellation updated = Constellation.builder()
				.name("DuplicateName")
				.build();
		updated.setId(1L);

		when(constellationRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(constellationRepository.existsByName("DuplicateName")).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> constellationEntityService.save(updated));
		verify(constellationRepository, never()).save(any());
	}

	@Test
	@DisplayName("delete должен удалять существующую группировку")
	void delete_ExistingId_Success() {
		when(constellationRepository.existsById(1L)).thenReturn(true);
		doNothing().when(constellationRepository).deleteById(1L);

		constellationEntityService.delete(1L);

		verify(constellationRepository).existsById(1L);
		verify(constellationRepository).deleteById(1L);
	}

	@Test
	@DisplayName("delete должен бросать исключение при удалении несуществующей группировки")
	void delete_NonExistingId_ThrowsEntityNotFoundException() {
		when(constellationRepository.existsById(99L)).thenReturn(false);

		assertThrows(EntityNotFoundException.class, () -> constellationEntityService.delete(99L));
		verify(constellationRepository, never()).deleteById(any());
	}
}
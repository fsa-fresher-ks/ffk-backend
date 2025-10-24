package fsa.fresher.pos.user;

import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.api.dto.UserUpdateDto;
import fsa.fresher.pos.common.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserServiceImpl service;

    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        // prepare services
//        repo = Mockito.mock(UserRepository.class);
//        service = new UserServiceImpl(repo);

        // prepare test data
        userCreateDto = new UserCreateDto();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create_whenEmailIsEmpty_shouldReturnUserEntity() {
        // Arrange
        userCreateDto.setEmail("abc@gmail.com");
        when(repo.existsByEmailIgnoreCase(userCreateDto.getEmail()))
                .thenReturn(false);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        when(repo.save(ArgumentMatchers.any()))
                .thenReturn(userEntity);

        // Act
        UserEntity result = service.create(userCreateDto);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
    }

    @Test
    void create_whenEmailIsExist_shouldThrowApiException() {
        // Arrange
        userCreateDto.setEmail("abc@gmail.com");
        when(repo.existsByEmailIgnoreCase(userCreateDto.getEmail()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ApiException.class, () -> service.create(userCreateDto));
    }

    @Test
    void list() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity e1 = new UserEntity();
        Page<UserEntity> page = new PageImpl<>(List.of(e1), pageable, 1);
        when(repo.findAll(pageable)).thenReturn(page);

        // Act
        Page<UserEntity> result = service.list(pageable);

        // Assert
        assertSame(page, result);
        verify(repo, times(1)).findAll(pageable);
    }

    @Test
    void search() {
        // Arrange
        String q = "john";
        Pageable pageable = PageRequest.of(0, 5);
        UserEntity e1 = new UserEntity();
        Page<UserEntity> page = new PageImpl<>(List.of(e1), pageable, 1);
        when(repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable))
                .thenReturn(page);

        // Act
        Page<UserEntity> result = service.search(q, pageable);

        // Assert
        assertSame(page, result);
        verify(repo, times(1)).findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
    }

    @Test
    void findById() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity e = new UserEntity();
        e.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(e));

        // Act
        Optional<UserEntity> result = service.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void update() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existing = new UserEntity();
        existing.setId(id);
        existing.setEmail("old@example.com");
        existing.setName("Old Name");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("New Name");
        // no email change, so no existsByEmailIgnoreCase check needed

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(repo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity updated = service.update(id, dto);

        // Assert
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertEquals("New Name", saved.getName());
        assertEquals(existing.getEmail(), saved.getEmail());
        assertEquals(updated.getName(), "New Name");
    }

    @Test
    void delete_whenIdExist_shouldDelete() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repo.existsById(id))
                .thenReturn(true);

        // Act
        service.delete(id);

        // Assert
        Mockito.verify(repo, Mockito.times(1)).deleteById(id);
    }

    @Test
    void delete_whenIdNotExist_shouldThrowApiException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repo.existsById(id))
                .thenReturn(false);

        // Act & Assert
        assertThrows(ApiException.class, () -> service.delete(id));
        verify(repo, never()).deleteById(id);
    }

    @Test
    void findByEmail() {
        // Arrange
        String email = "user@example.com";
        UserEntity e = new UserEntity();
        e.setEmail(email);
        when(repo.findByEmailIgnoreCase(email)).thenReturn(Optional.of(e));

        // Act
        Optional<UserEntity> result = service.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(repo, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    void update_whenUserNotFound_shouldThrowApiException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Any");

        // Act & Assert
        assertThrows(ApiException.class, () -> service.update(id, dto));
        verify(repo, never()).save(any(UserEntity.class));
    }

    @Test
    void update_whenEmailChangedToExisting_shouldThrowApiException() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existing = new UserEntity();
        existing.setId(id);
        existing.setEmail("old@example.com");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("taken@example.com");

        when(repo.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(ApiException.class, () -> service.update(id, dto));
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).existsByEmailIgnoreCase("taken@example.com");
        verify(repo, never()).save(any(UserEntity.class));
    }

    @Test
    void update_whenEmailSameIgnoringCase_shouldNotCheckExists_andUpdatesCase() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existing = new UserEntity();
        existing.setId(id);
        existing.setEmail("User@Example.com");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("user@example.com"); // same ignoring case

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(repo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity updated = service.update(id, dto);

        // Assert
        verify(repo, times(1)).findById(id);
        verify(repo, never()).existsByEmailIgnoreCase(anyString());
        verify(repo, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertEquals("user@example.com", saved.getEmail());
        assertEquals("user@example.com", updated.getEmail());
    }

    @Test
    void update_whenAllFieldsProvided_shouldUpdateAll() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existing = new UserEntity();
        existing.setId(id);
        existing.setEmail("old@example.com");
        existing.setName("Old");
        existing.setPassword("oldpassword");
        existing.setRole(UserRole.staff_sale);
        existing.setStatus(UserStatus.inactive);
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("New Name");
        dto.setEmail("new@example.com");
        dto.setPassword("newpassword");
        dto.setRole(UserRole.admin);
        dto.setStatus(UserStatus.active);

        when(repo.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(repo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity updated = service.update(id, dto);

        // Assert
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).existsByEmailIgnoreCase("new@example.com");
        verify(repo, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertEquals("New Name", saved.getName());
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("newpassword", saved.getPassword());
        assertEquals(UserRole.admin, saved.getRole());
        assertEquals(UserStatus.active, saved.getStatus());
        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("newpassword", updated.getPassword());
        assertEquals(UserRole.admin, updated.getRole());
        assertEquals(UserStatus.active, updated.getStatus());
    }
}
package fsa.fresher.pos.user;

import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.common.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    }

    @Test
    void search() {
    }

    @Test
    void findById() {
    }

    @Test
    void update() {
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
    }
}
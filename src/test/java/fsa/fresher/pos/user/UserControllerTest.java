package fsa.fresher.pos.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.api.dto.UserUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static UserEntity user(String name, String email) {
        UserEntity e = new UserEntity();
        e.setName(name);
        e.setEmail(email);
        e.setPassword("password123");
        e.setRole(UserRole.admin);
        e.setStatus(UserStatus.active);
        return e;
    }

    @BeforeEach
    void init() {
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/users returns page with metadata and content")
    void list_shouldReturnPagedUsers() throws Exception {
        // Arrange: seed 3 users and assert second page size 2 sorted by name desc has 1 element
        userRepository.saveAll(List.of(
                user("Adam", "adam@example.com"),
                user("John Doe", "john@example.com"),
                user("Zoe", "zoe@example.com")
        ));

        // Act & Assert
        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "name,desc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Adam")))
                .andExpect(jsonPath("$.content[0].email", is("adam@example.com")))
                .andExpect(jsonPath("$.metadata.page", is(1)))
                .andExpect(jsonPath("$.metadata.size", is(2)))
                .andExpect(jsonPath("$.metadata.totalElements", is(3)))
                .andExpect(jsonPath("$.metadata.totalPages", is(2)))
                .andExpect(jsonPath("$.metadata.sort[0]", is("name")))
                .andExpect(jsonPath("$.metadata.sort[1]", is("desc")));
    }

    @ParameterizedTest
    @MethodSource("searchData")
    @DisplayName("GET /api/users/search returns page with content for multiple queries")
    void search_shouldReturnPagedUsers_multiDataset(String query, int expectedCount) throws Exception {
        // Arrange
        userRepository.saveAll(List.of(
                user("Jane", "jane@example.com"),
                user("John", "john@example.com")
        ));

        // Act & Assert
        mockMvc.perform(get("/api/users/search")
                        .param("q", query)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedCount)))
                .andExpect(jsonPath("$.metadata.page", is(0)))
                .andExpect(jsonPath("$.metadata.size", is(20)))
                .andExpect(jsonPath("$.metadata.totalElements", is(expectedCount)))
                .andExpect(jsonPath("$.metadata.totalPages", is(expectedCount == 0 ? 0 : 1)));
    }

    private static Stream<Arguments> searchData() {
        return Stream.of(
                Arguments.of("ja", 1),
                Arguments.of("jo", 1),
                Arguments.of("x", 2)
        );
    }

    @Test
    @DisplayName("GET /api/users/{id} returns user when found")
    void get_shouldReturnUser_whenFound() throws Exception {
        // Arrange
        UserEntity saved = userRepository.save(user("Alex", "alex@example.com"));

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().toString())))
                .andExpect(jsonPath("$.name", is("Alex")))
                .andExpect(jsonPath("$.email", is("alex@example.com")));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns 404 when not found")
    void get_shouldReturn404_whenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    @DisplayName("POST /api/users creates and returns 201")
    void create_shouldReturn201_andUserDto() throws Exception {
        // Arrange
        UserCreateDto req = new UserCreateDto();
        req.setName("New User");
        req.setEmail("new@example.com");
        req.setPassword("password123");
        req.setRole(UserRole.admin);
        req.setStatus(UserStatus.active);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }

    @Test
    @DisplayName("POST /api/users returns 400 when email exists")
    void create_shouldReturn400_whenEmailExists() throws Exception {
        // Arrange existing user
        userRepository.save(user("Existing", "dup@example.com"));
        UserCreateDto req = new UserCreateDto();
        req.setName("New User");
        req.setEmail("dup@example.com");
        req.setPassword("password123");
        req.setRole(UserRole.admin);
        req.setStatus(UserStatus.active);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("EMAIL_EXISTS")))
                .andExpect(jsonPath("$.message", is("Email already in use")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} returns updated user")
    void update_shouldReturnUpdatedUser() throws Exception {
        // Arrange
        UserEntity existing = userRepository.save(user("Old Name", "old@example.com"));
        UserUpdateDto req = new UserUpdateDto();
        req.setName("Updated Name");
        req.setEmail("updated@example.com");

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existing.getId().toString())))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} returns 404 when user not found")
    void update_shouldReturn404_whenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        UserUpdateDto req = new UserUpdateDto();
        req.setName("X");

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} returns 204 on success")
    void delete_shouldReturn204() throws Exception {
        // Arrange: create a user and delete it
        UserEntity saved = userRepository.save(user("To Delete", "del@example.com"));

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} returns 404 when not found")
    void delete_shouldReturn404_whenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("User not found")));
    }
}
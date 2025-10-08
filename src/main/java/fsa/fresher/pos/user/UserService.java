package fsa.fresher.pos.user;

import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.api.dto.UserUpdateDto;
import fsa.fresher.pos.common.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public UserEntity create(UserCreateDto dto) {
        if (repo.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException("EMAIL_EXISTS", "Email already in use", HttpStatus.BAD_REQUEST);
        }
        UserEntity e = new UserEntity();
        e.setName(dto.getName());
        e.setEmail(dto.getEmail());
        e.setPassword(dto.getPassword());
        e.setRole(dto.getRole());
        e.setStatus(dto.getStatus());
        return repo.save(e);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> search(String q, Pageable pageable) {
        return repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findById(UUID id) {
        return repo.findById(id);
    }

    @Transactional
    public UserEntity update(UUID id, UserUpdateDto dto) {
        UserEntity e = repo.findById(id).orElseThrow(() ->
                new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(e.getEmail())
                && repo.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException("EMAIL_EXISTS", "Email already in use", HttpStatus.BAD_REQUEST);
        }
        if (dto.getName() != null) e.setName(dto.getName());
        if (dto.getEmail() != null) e.setEmail(dto.getEmail());
        if (dto.getPassword() != null) e.setPassword(dto.getPassword());
        if (dto.getRole() != null) e.setRole(dto.getRole());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
        return repo.save(e);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return repo.findByEmailIgnoreCase(email);
    }
}
package fsa.fresher.pos.user;

import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.api.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

interface UserService {
    UserEntity create(UserCreateDto dto);

    Page<UserEntity> list(Pageable pageable);

    Page<UserEntity> search(String q, Pageable pageable);

    Optional<UserEntity> findById(UUID id);

    UserEntity update(UUID id, UserUpdateDto dto);

    void delete(UUID id);

    Optional<UserEntity> findByEmail(String email);
}

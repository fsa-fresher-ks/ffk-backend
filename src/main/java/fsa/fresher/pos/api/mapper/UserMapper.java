package fsa.fresher.pos.api.mapper;

import fsa.fresher.pos.api.dto.UserCreateDto;
import fsa.fresher.pos.api.dto.UserDto;
import fsa.fresher.pos.api.dto.UserUpdateDto;
import fsa.fresher.pos.user.UserEntity;

public class UserMapper {
    public static UserDto toDto(UserEntity e) {
        UserDto d = new UserDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setEmail(e.getEmail());
        d.setRole(e.getRole());
        d.setStatus(e.getStatus());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public static UserEntity fromCreate(UserCreateDto c) {
        UserEntity e = new UserEntity();
        e.setName(c.getName());
        e.setEmail(c.getEmail());
        e.setPassword(c.getPassword());
        e.setRole(c.getRole());
        e.setStatus(c.getStatus());
        return e;
    }

    public static void applyUpdate(UserEntity e, UserUpdateDto u) {
        if (u.getName() != null) e.setName(u.getName());
        if (u.getEmail() != null) e.setEmail(u.getEmail());
        if (u.getPassword() != null) e.setPassword(u.getPassword());
        if (u.getRole() != null) e.setRole(u.getRole());
        if (u.getStatus() != null) e.setStatus(u.getStatus());
    }
}

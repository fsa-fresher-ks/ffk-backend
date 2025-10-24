package fsa.fresher.pos.user;

import fsa.fresher.pos.api.dto.*;
import fsa.fresher.pos.api.mapper.UserMapper;
import fsa.fresher.pos.common.ApiException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl service;

    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageUserDto> list(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(name = "sort", required = false) List<String> sortParams) {
        Pageable pageable = buildPageable(page, size, sortParams);
        Page<UserEntity> result = service.list(pageable);
        return ResponseEntity.ok(toPageDto(result, sortParams));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreateDto request) {
        UserEntity created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(created));
    }

    @GetMapping("/search")
    public ResponseEntity<PageUserDto> search(@RequestParam("q") String q,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(name = "sort", required = false) List<String> sortParams) {
        Pageable pageable = buildPageable(page, size, sortParams);
        Page<UserEntity> result = service.search(q, pageable);
        return ResponseEntity.ok(toPageDto(result, sortParams));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") UUID id) {
        UserEntity e = service.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(UserMapper.toDto(e));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDto request) {
        UserEntity updated = service.update(id, request);
        return ResponseEntity.ok(UserMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable buildPageable(int page, int size, List<String> sortParams) {
        Sort sort = Sort.unsorted();
        if (sortParams != null && !sortParams.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (int i = 0; i < sortParams.size(); i++) {
                String token = sortParams.get(i);
                String property;
                Sort.Direction dir = Sort.Direction.ASC;

                if (token.contains(",")) {
                    String[] parts = token.split(",");
                    property = parts[0];
                    if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                        dir = Sort.Direction.DESC;
                    }
                } else {
                    property = token;
                    if (i + 1 < sortParams.size()) {
                        String next = sortParams.get(i + 1);
                        if ("desc".equalsIgnoreCase(next)) {
                            dir = Sort.Direction.DESC;
                            i++; // consume direction token
                        } else if ("asc".equalsIgnoreCase(next)) {
                            dir = Sort.Direction.ASC;
                            i++; // consume direction token
                        }
                    }
                }
                orders.add(new Sort.Order(dir, property));
            }
            sort = Sort.by(orders);
        }
        return PageRequest.of(page, size, sort);
    }

    private PageUserDto toPageDto(Page<UserEntity> page, List<String> sortParams) {
        PageUserDto dto = new PageUserDto();
        dto.setContent(page.getContent().stream().map(UserMapper::toDto).collect(Collectors.toList()));
        PageMetadataDto md = new PageMetadataDto();
        md.setPage(page.getNumber());
        md.setSize(page.getSize());
        md.setTotalElements(page.getTotalElements());
        md.setTotalPages(page.getTotalPages());
        md.setSort(sortParams);
        dto.setMetadata(md);
        return dto;
    }
}

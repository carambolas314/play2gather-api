package com.play2gather.iam.infrastructure.adapter.inbound.web;

import com.play2gather.iam.common.dto.request.UserRegisterRequest;
import com.play2gather.iam.common.dto.request.UserUpdateRequest;
import com.play2gather.iam.common.dto.response.DataResponse;
import com.play2gather.iam.common.dto.response.Response;
import com.play2gather.iam.common.dto.response.UserResponse;
import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.domain.port.in.DeleteUserUseCase;
import com.play2gather.iam.domain.port.in.ProfileUserUseCase;
import com.play2gather.iam.domain.port.in.RegisterUserUseCase;
import com.play2gather.iam.domain.port.in.UpdateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iam/user")
public class UserController {
    @Autowired
    private RegisterUserUseCase registerUserUseCase;
    @Autowired
    private UpdateUserUseCase updateUserUseCase;

    @Autowired
    private DeleteUserUseCase deleteUserUseCase;

    @Autowired
    private ProfileUserUseCase profileUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserRegisterRequest request) {
        User saved = registerUserUseCase.register(request);
        Response response = new Response(saved.getId().toString(), "Usuário registrado com sucesso");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<DataResponse<UserResponse>> update(@Valid @RequestBody UserUpdateRequest request) {

        User updated = updateUserUseCase.update(getUserIdByAuthentication(), request);

        DataResponse<UserResponse> response = new DataResponse<>(updated.getId().toString(), "Usuário atualizado com sucesso", toResponse(updated));

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        User userProfile = profileUserUseCase.getProfile(getUserIdByAuthentication());
        return new ResponseEntity<>(toResponse(userProfile), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Response> delete() {
        deleteUserUseCase.deleteById(getUserIdByAuthentication());

        Response response = new Response(getUserIdByAuthentication().toString(), "Usuário removido com sucesso");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }

    private Long getUserIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getCredentials();
    }
}
package com.play2gather.iam.application.service;

import com.play2gather.iam.application.exception.CustomException;
import com.play2gather.iam.application.exception.ResourceNotFoundException;
import com.play2gather.iam.common.dto.request.UserRegisterRequest;
import com.play2gather.iam.common.dto.request.UserUpdateRequest;
import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.domain.model.UserRole;
import com.play2gather.iam.domain.port.in.*;
import com.play2gather.iam.domain.port.out.UserRepositoryPort;
import com.play2gather.iam.domain.port.out.UserRoleRepositoryPort;
import com.play2gather.iam.infrastructure.adapter.outbound.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService implements RegisterUserUseCase, DeleteUserUseCase, UpdateUserUseCase, ProfileUserUseCase {

    @Autowired
    private UserRepositoryPort userRepository;
    @Autowired
    private UserRoleRepositoryPort userRoleRepository;

    @Override
    public User register(UserRegisterRequest requestUser) {

        if (userRepository.findByEmail(requestUser.getEmail()).isPresent()) {
            throw new CustomException(HttpStatus.CONFLICT.value(), "Email indisponível", "Por favor, escolha um endereço de email diferente.");
        }

        User newUser = new User();
        newUser.setName(requestUser.getName());
        newUser.setEmail(requestUser.getEmail());
        newUser.setPassword(PasswordUtil.encryptPassword((requestUser.getPassword())));

        User savedUser = userRepository.save(newUser);

        UserRole newRole = new UserRole();
        newRole.setIdUser(savedUser.getId());
        newRole.setRole("USER");

        userRoleRepository.save(newRole);

        return savedUser;
    }

    @Override
    public User update(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userUpdateRequest.getName() != null && !userUpdateRequest.getName().isEmpty()) {
            user.setName(userUpdateRequest.getName());
        }
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty()) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encryptPassword(userUpdateRequest.getPassword()));
        }

        return userRepository.save(user);
    }


    @Override
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public void deleteById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.deleteById(user.getId());

        if (userRepository.findById(userId).isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Falha ao realizar a ação", "Erro no servidor, tente novamente mais tarde");
        }
    }
}

package kh.com.shoeshub.features.user.mapper;

import kh.com.shoeshub.features.user.User;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;

import java.util.Locale;

public class UserMapper {
    public User toEntity(CreateUserRequest request) {
        User user = new User();

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setPhone(request.phone());
        user.setDateOfBirth(request.dob());
        user.setGender(request.gender());
        user.setAddress(request.address());

        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }

        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        if (request.dateOfBirth() != null) {
            user.setDateOfBirth(request.dateOfBirth());
        }

        if (request.gender() != null) {
            user.setGender(request.gender().toUpperCase(Locale.ROOT));
        }

        if (request.address() != null) {
            user.setAddress(request.address());
        }
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getPhone(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getAddress(),
                user.getRole(),
                user.getCreatedAt()
        );

        return response;
    }
}

package com.auth.app.controllers;

import com.auth.app.domain.model.UserModel;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.services.application.AuthService;
import com.auth.app.services.domain.UserService;
import com.auth.app.utils.ApiResponseFactory;
import com.auth.app.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.exception.exceptions.MissingOrganizationException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/staff", "/staff/"})
@RequiredArgsConstructor
public class StaffController {

    private final AuthService authService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStaff(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        // Authenticate and get current user
        UserResponse currentUser = authService.getCurrentUser(authorizationHeader, RequestUtils.extractIp(httpRequest), RequestUtils.extractUserAgent(httpRequest));
        
        // Get organization ID
        UUID organizationId = currentUser.getOrganizationId();
        
        if (organizationId == null) {
             // For now, if no organization, return empty or self? 
             // Ideally we should assign organization to user implicitly. 
             // As user IS the organization owner in current context (likely).
             // But following the plan, we just return empty or throw error if not present.
             // Let's assume user.id IS the organization_id for OWNER role if not set? 
             // No, let's stick to the plan: organization_id field.
             // If null, return empty list.
             return ApiResponseFactory.success("No organization found for user", java.util.Collections.emptyList());
        }

        List<UserModel> staffMembers = userService.getStaffByOrganization(organizationId);
        List<UserResponse> response = staffMembers.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());

        return ApiResponseFactory.success("Staff members retrieved successfully", response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        UserResponse currentUser = authService.getCurrentUser(authorizationHeader, RequestUtils.extractIp(httpRequest), RequestUtils.extractUserAgent(httpRequest));
        
        if (currentUser.getOrganizationId() == null) {
             throw new MissingOrganizationException();
        }

        UserModel userModel = modelMapper.map(request, UserModel.class);
        userModel.setOrganizationId(currentUser.getOrganizationId());
        
        UserModel createdUser = userService.createStaff(userModel);
        
        UserResponse response = modelMapper.map(createdUser, UserResponse.class);
        return ApiResponseFactory.created("Staff member created successfully", response);
    }
}

package com.balazsh.inventory.domain.model;

import com.balazsh.inventory.entity.Role;

public record UserRegisterDetails(String username, String password, String role) {
}

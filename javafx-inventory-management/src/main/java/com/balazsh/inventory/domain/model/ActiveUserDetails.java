package com.balazsh.inventory.domain.model;

import com.balazsh.inventory.util.enums.OPERATION;
import com.balazsh.inventory.util.enums.RESOURCE;

import java.util.List;
import java.util.Map;

public record ActiveUserDetails(String username, Map<RESOURCE, List<OPERATION>> permissions, String[] roles){

}

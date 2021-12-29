package com.ase.userservice.service;

import com.ase.client.com.ase.contract.ResponseMessage;
import com.ase.client.com.ase.contract.UserDto;
import com.ase.userservice.dto.RegistrationDto;

import javax.servlet.Registration;
import java.util.List;

public interface UserService {

    ResponseMessage save(RegistrationDto user, String role);

    UserDto getById(String id);

    List<UserDto> getAll();



    Boolean deleteUser(String id);


    UserDto updateUser(UserDto user,String id);

    List<UserDto> getAllByRole(String role);



}

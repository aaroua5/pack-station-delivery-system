package com.ase.userservice.controller;


import com.ase.client.com.ase.contract.ResponseMessage;
import com.ase.userservice.dto.RegistrationDto;
import com.ase.userservice.service.UserService;
import com.ase.client.com.ase.contract.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deletebyId (@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> getOne(@PathVariable String id) {
        log.warn("User:getOne method is on. ID:"+id);
        return ResponseEntity.ok(userService.getById(id));
    }



    @GetMapping(value = "/all")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> data = userService.getAll();
        return ResponseEntity.ok(data);
    }


    @PutMapping(value = "/update/{id}")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto, @PathVariable String id) {
        UserDto user = userService.updateUser(userDto,id);
        if(user==null)
            return ResponseEntity.badRequest().body(user);
        return ResponseEntity.ok(user);
    }


    @PostMapping(value = "customer/add")
    public ResponseEntity<ResponseMessage> addCustomer(@RequestHeader(value = "Cookie", required = true) String cookie, @RequestBody RegistrationDto registrationDto){

        return ResponseEntity.ok(userService.save(registrationDto,"CUSTOMER",cookie));
    }


    @PostMapping(value = "deliverer/add")
    public ResponseEntity<ResponseMessage> addDeliverer(@RequestHeader(value = "Cookie", required = true) String cookie, @RequestBody RegistrationDto registrationDto){
        return ResponseEntity.ok(userService.save(registrationDto,"DELIVERER",cookie));
    }


    @PostMapping(value = "dispatcher/add")
    public ResponseEntity<ResponseMessage> addDispatcher(@RequestHeader(value = "Cookie", required = true) String cookie, @RequestBody RegistrationDto registrationDto){
        return ResponseEntity.ok(userService.save(registrationDto,"DISPATCHER",cookie));
    }


    @GetMapping(value = "/dispatcher/all")
    public ResponseEntity<List<UserDto>> getAllDispatchers(){
        List<UserDto> data = userService.getAllByRole("DISPATCHER");
        return ResponseEntity.ok(data);
    }

    @GetMapping(value = "/customer/all")
    public ResponseEntity<List<UserDto>> getAllcustomers(){
        List<UserDto> data = userService.getAllByRole("CUSTOMER");
        return ResponseEntity.ok(data);
    }

    @GetMapping(value = "/deliverer/all")
    public ResponseEntity<List<UserDto>> getallDeliverers(){
        List<UserDto> data = userService.getAllByRole("DELIVERER");
        return ResponseEntity.ok(data);
    }


    @GetMapping(value = "/dispatcher/{id}")
    public ResponseEntity<UserDto> getDispatcher(@PathVariable String id) {
        UserDto userDto = userService.getById(id);
        if(userDto==null)
            return ResponseEntity.badRequest().body(userDto);
        return ResponseEntity.ok(userDto);
    }


    @GetMapping(value = "/customer/{id}")
    public ResponseEntity<UserDto> getCustomer(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        UserDto userDto = userService.getById(id);
        if(userDto==null)
            return ResponseEntity.badRequest().body(userDto);
        if(!authorities.equals("ROLE_DISPATCHER")) {
            if (!userDto.getUsername().equals(username)) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        return ResponseEntity.ok(userDto);
    }


    @GetMapping(value = "/deliverer/{id}")
    public ResponseEntity<UserDto> getDeliverer(@PathVariable String id) {
        UserDto userDto = userService.getById(id);
        if(userDto==null)
            return ResponseEntity.badRequest().body(userDto);
        return ResponseEntity.ok(userDto);
    }
}

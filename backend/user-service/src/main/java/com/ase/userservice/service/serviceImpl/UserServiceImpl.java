package com.ase.userservice.service.serviceImpl;


import com.ase.client.AuthServiceClient;
import com.ase.client.com.ase.contract.ResponseMessage;
import com.ase.userservice.dto.RegistrationDto;
import com.ase.userservice.entity.User;
import com.ase.userservice.repository.UserRepository;
import com.ase.userservice.service.UserService;
import com.ase.client.com.ase.contract.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Override
    public UserDto getById(String id) {

        if(!userRepository.existsById(new ObjectId(id)))
            return null;

        User tempUser = userRepository.findById(new ObjectId(id));


        if(tempUser==null){
            log.warn("user can't be found");
            return null;
        }
        return modelMapper.map(tempUser, UserDto.class);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> data = userRepository.findAll();
        if(data==null)
            return null;
        return Arrays.asList(modelMapper.map(data, UserDto[].class));
    }


    @Override
    public Boolean deleteUser(String id) {
        if(userRepository.existsById(new ObjectId(id))){
            userRepository.deleteById(id);
            return true;
        }
        else{
            return false;
        }

    }


    @Override
    @Transactional
    public ResponseMessage save(RegistrationDto registrationDto, String role) {
        log.warn("UserService: save method is on");
        ResponseMessage saveResp = new ResponseMessage();
        registrationDto.setEmail(registrationDto.getEmail().toLowerCase());
        if(userRepository.existsByUsername(registrationDto.getUsername())){
            saveResp.setResponseType(0);
            saveResp.setResponseMessage("This username already is in use. Please enter a new one");
            return saveResp;
        }

        if(userRepository.existsByEmail(registrationDto.getEmail())){
            saveResp.setResponseType(1);
            saveResp.setResponseMessage("This e-mail already is in use. Please enter a new one");
            return saveResp;

        }

        User tempuser = modelMapper.map(registrationDto, User.class);
        tempuser.setRole(role);

        /*
        //SAVE BY USING AUTH'S REGISTER METHOD
        String token = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJyb2xlcyI6W3sicm9sZSI6IlJPTEVfQ1VTVE9NRVIifV0sInN1YiI6InRlc3QxIiwiaXNzIjoiYXNlRGVsaXZlcnkiLCJpYXQiOjE2Mzk4MjIxMjgsImV4cCI6MTYzOTg0MDEyOH0.OsPDnG2AT6CSKHQXSWh0EUQ8ZvQ_ZrvsbizliHjX2kExQKhqG1z5Q-1Wu6IxQgVf1qSg56shzIun7n_dGnrprpG3NaPOBOkTDVe0kNfgjyeYpmKem77MjTnIfzSaZqUOjVM3y69JiN8ubTxW2gQ0jYZO_GihhHNbbfisJYH0wKl7A1QdgM0bLzAvindizXFDeag1gmHeijImt6lfu3aOM9T2GBJ4iSx8a6B4_zjFXTHGhiBQhr3dsDW2aIj_NHkBReqBYzP08Y4O2emSfjvisxLZtothb0FYMgF4304dUE3oJNLF-kav1oDR-W2AA4FoXDiHtW2E7An8RpP-zhw5Qw";
        userDto.setRole(role);
        UserDto dbUser = authServiceClient.register(token,userDto).getBody();
        */

        try {
            userRepository.save(tempuser);
            //UserDto dbUser = authServiceClient.register(userDto).getBody();
        }
        catch (Exception e){
            log.warn("exception trace:",e.getStackTrace());
            saveResp.setResponseType(0);
            saveResp.setResponseMessage("Unexpected problem occured! Please try again layer");
            return saveResp;
        }
        saveResp.setResponseType(1);
        saveResp.setResponseMessage("Successfull registration!");
        return saveResp;

    }





    @Override
    public UserDto updateUser(UserDto tempUser,String id){
        User dbUser = userRepository.findById(new ObjectId(id));
        if(!dbUser.getEmail().equals(tempUser.getEmail()) ){ //check if e-mail is already used by another user
            if(userRepository.existsByEmail(tempUser.getEmail().toLowerCase())){

                return null;
            }
        }

        dbUser.setSurname(tempUser.getSurname());
        dbUser.setFirstName(tempUser.getFirstName());
        dbUser.setEmail(tempUser.getEmail().toLowerCase());
        userRepository.save(dbUser);
        return tempUser;

    }

    @Override
    public List<UserDto> getAllByRole(String role) {

        List<User> users = userRepository.findAllByRole(role);
        if(users==null)
            return null;

        return Arrays.asList(modelMapper.map(users, UserDto[].class));

    }


}

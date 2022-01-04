package com.ase.authservice.service.serviceimpl;

import com.ase.authservice.config.CookieConfig;
import com.ase.authservice.dto.AuthDto;

import com.ase.authservice.entity.User;
import com.ase.authservice.jwt.JwtUtil;
import com.ase.authservice.repository.UserRepository;
import com.ase.authservice.service.AuthService;
import com.ase.client.com.ase.contract.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieConfig cookieConfig;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        encoder.encode(user.getPassword()),
                        getAuthorities(user));

        return springUser;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        list.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        return list;
    }

    @Override
    @Transactional
    public UserDto register(AuthDto authDto) {
        log.warn("Auth Service: Register method is on");
        User tempUser = modelMapper.map(authDto, User.class);
        log.warn("Auth Service: Mapper works fine");
        tempUser.setRole("user");
        tempUser = userRepository.save(tempUser);
        log.warn("Auth Service: Repo works fine");
        UserDto userDto = modelMapper.map(tempUser, UserDto.class);
        return userDto;
    }



    public ResponseEntity<String> authenticateUser(String authorization,
                                                   HttpServletRequest request, HttpServletResponse response) throws Exception {



        // decodes request header and splits into username/pw
        String headerString = authorization.substring("Basic".length()).trim();
        String decoded = new String(Base64.getDecoder().decode(headerString));
        String username = decoded.split(":", 2)[0];
        String password = decoded.split(":", 2)[1];

        // get user by given username
        UserDetails user = loadUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("Email or password is incorrect", HttpStatus.BAD_REQUEST);
        }

        // authenticate using authManager and token of username and password
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        Authentication auth = null;

        try{

            auth = authManager.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(auth);

            if(auth != null) {
                final String jwt = jwtUtil.generateToken(user);

                Cookie jwtCookie = cookieConfig.createCookie("jwt", jwt);

                response.addCookie(jwtCookie);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Email or password is incorrect", HttpStatus.BAD_REQUEST);
            }
        } catch (BadCredentialsException e){
            e.printStackTrace();
            return new ResponseEntity<>("Email or password is incorrect", HttpStatus.BAD_REQUEST);
        } catch (Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setAuthenticationToken(Authentication auth){
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Cookie[] cookies = request.getCookies();


        if(cookies == null){
            response.sendError(HttpStatus.FORBIDDEN.value(), "no cookie found in request");
        }
        else{
            Cookie jwtCookie = null;
            for(int i = 0; i < cookies.length; i++){
                if(cookies[i].getName().equals("jwt")){
                    jwtCookie = cookies[i];
                    break;
                }
            }
            if(jwtCookie != null) {
                Cookie deletedCookie = cookieConfig.createCookie("jwt", "deleted");
                deletedCookie.setMaxAge(5);
                log.warn("jwt value:" + jwtCookie.getValue());
                log.warn("jwt version:" + jwtCookie.getVersion());
                response.addCookie(deletedCookie);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            else{
                response.sendError(HttpStatus.FORBIDDEN.value(), "no JWTcookie found in request");
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

package com.example.springbootloginregistration.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.springbootloginregistration.registration.token.ConfirmationToken;
import com.example.springbootloginregistration.registration.token.ConfirmationTokenService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

  private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
  }

  public String signUpUser(User user) {
    boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
    
     if (userExists) {
      throw new IllegalStateException("email already taken");
    }

    String passwordEncoded = bCryptPasswordEncoder
      .encode(user.getPassword());

    user.setPassword(passwordEncoded);

    userRepository.save(user); 

    String token = UUID.randomUUID().toString();

    ConfirmationToken confirmationToken = new ConfirmationToken();
    confirmationToken.setToken(token);
    confirmationToken.setCreatedAt(LocalDateTime.now());
    confirmationToken.setExpiredAt(LocalDateTime.now().plusMinutes(15));
    confirmationToken.setUser(user);

    confirmationTokenService.saveConfirmationToken(confirmationToken);

    // TODO: Send email

    return token;
  }

  public int enableUser(String email) {
    return userRepository.enableUser(email);
  }

}

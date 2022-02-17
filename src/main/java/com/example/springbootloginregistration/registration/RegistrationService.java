package com.example.springbootloginregistration.registration;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import com.example.springbootloginregistration.registration.token.ConfirmationToken;
import com.example.springbootloginregistration.registration.token.ConfirmationTokenService;
import com.example.springbootloginregistration.user.User;
import com.example.springbootloginregistration.user.UserService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
  
  private final UserService userService;
  private final EmailValidator emailValidator;
  private final ConfirmationTokenService confirmationTokenService;

  public String register(RegistrationRequest request) {
    boolean isValidEmail = emailValidator.
      test(request.getEmail()); 
    
    if (!isValidEmail ) {
      throw new IllegalStateException("email not valid");
    }

    var user = new User();
    
    var fullName = new StringBuilder();
    fullName.append(request.getLastName());
    fullName.append(" ");
    fullName.append(request.getFirstName());
    
    user.setName(fullName.toString());
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());

    return userService.signUpUser(user);
  }

  @Transactional
  public String confirmToken(String token) {
    
    ConfirmationToken confirmationToken = confirmationTokenService
      .getToken(token)
      .orElseThrow(() -> 
        new IllegalStateException("token not found"));
      
    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException("email already confirmed");
    }

    LocalDateTime expiredAt = confirmationToken.getExpiredAt();

    if (expiredAt.isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("token expired");
    }

    confirmationTokenService.setConfirmedAt(token);
    userService.enableUser(
      confirmationToken.getUser().getEmail()
    );
    
    return "confirmed";
  
  }

}

package com.example.springbootloginregistration.registration;

import com.example.springbootloginregistration.user.User;
import com.example.springbootloginregistration.user.UserService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
  
  private final UserService userService;
  private final EmailValidator emailValidator;

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

}

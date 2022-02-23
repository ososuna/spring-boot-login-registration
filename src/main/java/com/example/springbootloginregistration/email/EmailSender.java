package com.example.springbootloginregistration.email;

public interface EmailSender {
  
  void send(String to, String email);

}

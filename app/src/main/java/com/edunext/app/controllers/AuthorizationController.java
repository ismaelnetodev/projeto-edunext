package com.edunext.app.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edunext.app.dtos.LoginDTO;
import com.edunext.app.dtos.LoginResponseDTO;
import com.edunext.app.model.Aluno;
import com.edunext.app.model.User;
import com.edunext.app.security.TokenService;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public AuthorizationController(AuthenticationManager authenticationManager, TokenService tokenService){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var user = (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);
        boolean senhaTemporaria = false;

        if (user instanceof Aluno aluno){
            senhaTemporaria = aluno.isSenhaTemporaria();
        }

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getRole().toString(), senhaTemporaria));
    }
}

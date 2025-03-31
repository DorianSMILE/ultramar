package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.ChangePasswordDTO;
import com.astartes.ultramar.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class FirstConnexionController {

    private final UserService userService;

    public FirstConnexionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/firstConnexion/{uuid}")
    public ResponseEntity<?> firstConnexion(@PathVariable @NotNull UUID uuid) {
        Optional<UUID> tokenOpt = userService.verifUuidExist(uuid);
        if (tokenOpt.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("uuid invalide ou expiré");
        String redirectUrl = "http://localhost:4200/admin/changePassword?uuid=" + uuid;
        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));
    }


    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO.uuid(), changePasswordDTO.password());
        return ResponseEntity.ok("redirectUrl");
    }
    // Ou add bool is changed pour redirect vers le change password avant de laisser le user navigué sur l'app
    // Add /sendMail pour send le lien avec l'uid lors du CreateUser

}

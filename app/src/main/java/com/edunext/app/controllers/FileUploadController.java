package com.edunext.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.edunext.app.dtos.FileUploadResponseDTO;
import com.edunext.app.services.StorageService;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private final StorageService storageService;

    public FileUploadController(StorageService storageService){
        this.storageService = storageService;
    }

    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file){
        try {
            String imageUrl = storageService.uploadFile(file);
            FileUploadResponseDTO responseDTO = new FileUploadResponseDTO(imageUrl);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o upload do arquivo: " + e.getMessage());
        }
    }
}

package com.example.learning_app.document.service;

import com.example.learning_app.document.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j

public class StorageService {
  private final Path rootStoragePath ;

  public StorageService(@Value("${application.storage.local-path}") String storagePath) {
      this.rootStoragePath = Paths.get(storagePath).toAbsolutePath().normalize();
      try{
          Files.createDirectories(this.rootStoragePath);
      } catch (IOException e) {
          log.error("Could not initialize storage directory!", e);
          throw new RuntimeException("Could not initialize storage directory",e);
      }
  }

  public String saveDocument(MultipartFile file, ) {

  }
}

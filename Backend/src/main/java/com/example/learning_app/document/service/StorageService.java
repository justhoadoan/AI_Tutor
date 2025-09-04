package com.example.learning_app.document.service;

import com.example.learning_app.document.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

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

  public String save(MultipartFile file, String semesterCode, String subjectName ) throws IOException {

      String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
      String uniqueFileName = System.currentTimeMillis() + "_" + originalFilename;
      Path targetDirectory = createAndGetTargetDirectory(semesterCode, subjectName);
      Path destinationPath = targetDirectory.resolve(uniqueFileName);
      Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
      return destinationPath.toString();
  }


  public String move(String oldFilePath, String newSemesterCode, String newSubjectName) throws IOException {
      Path sourcePath = Paths.get(oldFilePath);
      String filename = sourcePath.getFileName().toString();

      Path destinationDirectory = createAndGetTargetDirectory(newSemesterCode, newSubjectName);
      Path destinationPath = destinationDirectory.resolve(filename);

      Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
      log.info("Moved {} to {}", oldFilePath, newSubjectName);

      return destinationPath.toString();
  }

  public void delete(String filePath) throws IOException{
        if(filePath == null || filePath.isEmpty()){
            log.warn("Empty file path!");
            return;
        }

        Path pathToDelete = Paths.get(filePath);

        Files.deleteIfExists(pathToDelete);
        log.info("Deleted file {}", pathToDelete);
  }


  private Path createAndGetTargetDirectory(String semesterCode, String subjectName) throws IOException {
      String sanitizedSubjectFolderName = subjectName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
      Path targetDirectory = this.rootStoragePath.resolve(semesterCode).resolve(sanitizedSubjectFolderName);
      return Files.createDirectories(targetDirectory);
  }
}

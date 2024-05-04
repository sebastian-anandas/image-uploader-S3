package io.seb.imageuploader.controller;

import io.seb.imageuploader.exceptions.ImageUploadException;
import io.seb.imageuploader.services.S3ImageUploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/S3")
public class S3ImageUploaderController {

    private S3ImageUploader s3ImageUploader;

    public S3ImageUploaderController(S3ImageUploader s3ImageUploader) {
        this.s3ImageUploader = s3ImageUploader;
    }

    // upload image
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(MultipartFile file) throws ImageUploadException {
        return ResponseEntity.ok(s3ImageUploader.uploadImage(file));
    }

    // get all files Url
    @GetMapping("/image")
    public List<String> getAllFiles() {
        return s3ImageUploader.getAllFiles();
    }

    // get Url by filename
    @GetMapping("/{fileName}")
    public String getUrlByName(@PathVariable String fileName) {
        return s3ImageUploader.getImageUrlByName(fileName);
    }

}

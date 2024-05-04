package io.seb.imageuploader.services;

import io.seb.imageuploader.exceptions.ImageUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageUploader {

    String uploadImage(MultipartFile image) throws IOException, ImageUploadException;

    List<String> getAllFiles();

    String preSignedUrl(String filename);

    String getImageUrlByName(String filename);

}

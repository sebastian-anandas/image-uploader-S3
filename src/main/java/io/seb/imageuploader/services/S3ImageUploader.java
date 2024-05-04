package io.seb.imageuploader.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import io.seb.imageuploader.exceptions.ImageUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3ImageUploader implements ImageUploader {

    @Autowired
    private AmazonS3 amazonS3;

    public S3ImageUploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    @Value("${app.s3.bucket}")
    private String bucket;

    @Override
    public String uploadImage(MultipartFile image) throws ImageUploadException {

        if(image == null) {
            throw new ImageUploadException("Image Url is null!!");
        }

        // abc.png
        String actualFilename = image.getOriginalFilename();

        // new filename: ohasd2232.png - it will generate a random code and append the extension of actual image
        String fileName = UUID.randomUUID().toString() + actualFilename.substring(actualFilename.lastIndexOf("."));

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(image.getSize());

        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metaData));
            return this.preSignedUrl(fileName);
        } catch (IOException e) {
            throw new ImageUploadException("Error in umploading image " + e.getMessage());
        }

    }

    @Override
    public List<String> getAllFiles() {

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket);
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

        List<String> listFileUrls = objectSummaries.stream().map(item -> this.preSignedUrl(item.getKey())).collect(Collectors.toList());

        return listFileUrls;
    }

    @Override
    public String preSignedUrl(String filename) {

        Date expirationDate = new Date();

        int hour = 2;
        long time = expirationDate.getTime() + hour * 60 * 60 * 100;
//      time = time + hour * 60 * 60 * 100;
        expirationDate.setTime(time);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, filename)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expirationDate);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    @Override
    public String getImageUrlByName(String filename) {

        S3Object s3Object = amazonS3.getObject(bucket, filename);
        String url = preSignedUrl(s3Object.getKey());

        return url;
    }
}

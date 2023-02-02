package com.quack.talk.api.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public  class S3Util {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    // S3로 파일 셋팅
    public String upload(MultipartFile file, String dirName) {
        // S3에 저장된 파일 이름
        String fileName = dirName + "/"+getToday() + "/" + createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        // s3로 업로드
        try (InputStream inputStream = file.getInputStream()) {
            String uploadImageUrl = putS3(inputStream, objectMetadata, fileName);
            return uploadImageUrl;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    // S3로 업로드
    private String putS3(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    public void delete(String photoUrl) {
        String bucketUrl = "https://"+bucket+".s3.ap-northeast-2.amazonaws.com/";
        String key = photoUrl.substring(bucketUrl.length());
        deleteS3(key);
    }
    private void deleteS3(String key){
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
    }

    private String createFileName(String name){
        return UUID.randomUUID().toString().replace("-","")+"."+name;
    }
    private String getToday(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        return simpleDateFormat.format(System.currentTimeMillis());
    }
}

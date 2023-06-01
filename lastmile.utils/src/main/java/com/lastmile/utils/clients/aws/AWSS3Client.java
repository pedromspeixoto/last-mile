package com.lastmile.utils.clients.aws;

import com.amazonaws.services.s3.AmazonS3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.lastmile.utils.logs.CustomLogging;
 
@Service
public class AWSS3Client {

    @Autowired
    private CustomLogging logger;

    @Autowired
    private AmazonS3 s3client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    // @Async annotation ensures that the method is executed in a different background thread 
    // but not consume the main thread.
    @Async
    public boolean uploadFile(final MultipartFile multipartFile, final String newFilename) {
        logger.info("file upload in progress: " + newFilename);
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(bucketName, file, newFilename);
            logger.info("file upload is complete: " + newFilename);
            file.delete();  // to remove the file locally created in the project folder.
            return true;
        } catch (final AmazonServiceException ex) {
            logger.error("error processing file: " + newFilename + ". error message:" + ex.getMessage());
            return false;
        }
    }

    // @Async annotation ensures that the method is executed in a different background thread 
    // but not consume the main thread.
    @Async
    public boolean deleteFile(final String filename) {
        logger.info("delete file in progress: " + filename);
        try {
            deleteFileFromS3Bucket(bucketName, filename);
            logger.info("file delete is complete: " + filename);
            return true;
        } catch (final AmazonServiceException ex) {
            logger.error("error deleting file: " + filename + ". error message:" + ex.getMessage());
            return false;
        }
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            logger.error("error converting the multi-part file to file. error message: " + ex.getMessage());
        }
        return file;
    }
 
    private void uploadFileToS3Bucket(final String bucketName, final File file, final String uniqueFileName) {
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        s3client.putObject(putObjectRequest);
    }

    private void deleteFileFromS3Bucket(final String bucketName, final String uniqueFileName) {
        s3client.deleteObject(bucketName, uniqueFileName);
    }

    // @Async annotation ensures that the method is executed in a different background thread 
    // but not consume the main thread.
    @Async
    public byte[] downloadFile(final String keyName) {
        byte[] content = null;
        logger.info("downloading file: " + keyName);
        try {
            final S3Object s3Object = s3client.getObject(bucketName, keyName);
            final S3ObjectInputStream stream = s3Object.getObjectContent();    
            content = IOUtils.toByteArray(stream);
            logger.info("file downloaded successfully: " + keyName);
            s3Object.close();
        } catch(final Exception ex) {
            logger.info("error downloading file: " + keyName + ". error message: " + ex.getMessage());
        }
        return content;
    }

}
package com.ecom.service;

import com.ecom.exception.UploadFileErrorException;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String
    uploadFile(MultipartFile fileImg, String filename) throws UploadFileErrorException;
}

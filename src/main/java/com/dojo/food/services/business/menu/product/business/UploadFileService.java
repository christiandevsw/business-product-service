package com.dojo.food.services.business.menu.product.business;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface UploadFileService {

    Resource load(String route,String fileName) throws MalformedURLException;
    String copy(String route,MultipartFile file) throws IOException;
    void copy(String route,String uniqueFileName,MultipartFile file) throws IOException;
    boolean delete(String route,String fileName);
    String getUniqueFileName(MultipartFile file);
    String getFileNameBasedOnId(Long id,MultipartFile file);
    boolean verifyExistFile(String route, String fileName);


}

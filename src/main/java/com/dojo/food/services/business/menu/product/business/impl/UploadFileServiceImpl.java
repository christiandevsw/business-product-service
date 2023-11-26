package com.dojo.food.services.business.menu.product.business.impl;

import com.dojo.food.services.business.menu.product.business.UploadFileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Override
    public Resource load(String route, String fileName) throws MalformedURLException {
        Path pathPhoto = getPath(route, fileName);
        Resource resource = null;
        resource = new UrlResource(pathPhoto.toUri());
        if (!resource.exists() || !resource.isReadable())
            throw new RuntimeException("Error: no se puede cargar la imagen");
        return resource;
    }

    @Override
    public String copy(String route, MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path pathPhoto = getPath(route, uniqueFileName);
        Files.copy(file.getInputStream(), pathPhoto);
        return uniqueFileName;
    }

    @Override
    public boolean delete(String route,String fileName) {
        Path pathPhoto = getPath(route,fileName);
        File photo = pathPhoto.toFile();
        if (photo.exists()){
            if (photo.delete()) return true;
        }
        return false;
    }

    public Path getPath(String route, String fileName) {
        return Paths.get(route).resolve(fileName).toAbsolutePath();
    }
}

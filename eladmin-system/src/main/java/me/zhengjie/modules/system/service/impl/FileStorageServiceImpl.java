package me.zhengjie.modules.system.service.impl;

import me.zhengjie.domain.QiniuContent;
import me.zhengjie.modules.system.domain.FileNotFoundException;
import me.zhengjie.modules.system.service.FileStorageService;
import me.zhengjie.modules.system.utils.FileUtils;
import me.zhengjie.service.QiNiuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service(value = "fileService")
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private QiNiuService qiNiuService;

    @Override
    public QiniuContent storeFile(MultipartFile file) throws Exception {
//        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

//        if (!fileStorageProperties.isKeepName()){ //不保持文件名
//            fileName = FileUtils.randomFileName(fileName);
//        }
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//
//            // Copy file to the target location (Replacing existing file with the same name)
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
//        }
        fileName = FileUtils.randomFileName(fileName);
        return qiNiuService.upload(file,qiNiuService.find());
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws Exception {
        try {
            Path filePath = Paths.get(fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }


}

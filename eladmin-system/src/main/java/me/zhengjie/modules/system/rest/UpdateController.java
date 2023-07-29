package me.zhengjie.modules.system.rest;

import cn.hutool.core.util.ObjectUtil;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.domain.LocalStorage;
import me.zhengjie.domain.QiniuContent;
import me.zhengjie.modules.system.domain.AppVersionInfo;
import me.zhengjie.modules.system.domain.PageQuery;
import me.zhengjie.modules.system.service.FileStorageService;
import me.zhengjie.modules.system.service.UpdateService;
import me.zhengjie.modules.system.utils.BASE64DecodedMultipartFile;
import me.zhengjie.modules.system.utils.DateUtils;
import me.zhengjie.modules.system.utils.FileUtils;
import me.zhengjie.modules.system.utils.Md5Utils;
import me.zhengjie.utils.Result;
import me.zhengjie.utils.XUpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 版本更新api
 *
 * @author xuexiang
 * @since 2018/7/23 下午6:21
 */
@RestController
@RequestMapping(value = "/api/update")
public class UpdateController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @Autowired
    private UpdateService updateService;

    @Autowired
    private FileStorageService fileService;

    @ResponseBody
    @AnonymousGetMapping(value = "/checkVersion")
    public XUpdateResult doCheckVersion(int versionCode, String appKey) {
        AppVersionInfo latestAppVersionInfo = updateService.getLatestAppVersionInfo(versionCode, appKey);
        XUpdateResult xUpdateResult = new XUpdateResult();
        xUpdateResult.setCode(0);
        xUpdateResult.setMsg("");
        xUpdateResult.setUpdateStatus(latestAppVersionInfo.getUpdateStatus());
        xUpdateResult.setVersionCode(latestAppVersionInfo.getVersionCode());
        xUpdateResult.setVersionName(latestAppVersionInfo.getVersionName());
        xUpdateResult.setUploadTime(latestAppVersionInfo.getUploadTime());
        xUpdateResult.setModifyContent(latestAppVersionInfo.getModifyContent().replace("\\n","\n"));
        xUpdateResult.setDownloadUrl(latestAppVersionInfo.getDownloadUrl());
        xUpdateResult.setApkSize(latestAppVersionInfo.getApkSize());
        xUpdateResult.setApkMd5(latestAppVersionInfo.getApkMd5());
        return xUpdateResult;
    }

    @ResponseBody
    @RequestMapping(value = "/versionPageQuery", method = RequestMethod.POST)
    public Result pageQueryVersions(@RequestBody PageQuery pageQuery) {
        return Result.of(updateService.getAllAppVersionInfo(pageQuery.pageNum, pageQuery.pageSize));
    }

    @ResponseBody
    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public Result getAllVersions() {
        return Result.of(updateService.getAllAppVersionInfo());
    }

    @ResponseBody
    @RequestMapping(value = "/newVersion", method = RequestMethod.POST)
    @PreAuthorize("@el.check('newVersion:add')")
    public Result register(@RequestBody AppVersionInfo appVersionInfo) throws Exception {
        return addNewAppVersion(appVersionInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/delete")
    @PreAuthorize("@el.check('newVersion:del')")
    public Result deleteAppVersionInfo(@RequestBody AppVersionInfo appVersionInfo) throws Exception {
        return Result.of(updateService.deleteAppVersionInfo(appVersionInfo.getVersionId()));
    }

    @ResponseBody
    @RequestMapping(value = "/updateInfo")
    @PreAuthorize("@el.check('newVersion:update')")
    public Result updateAppVersionInfo(@RequestBody AppVersionInfo appVersionInfo) throws Exception {
        return Result.of(updateService.updateAppVersionInfo(appVersionInfo));
    }

    @ResponseBody
    @RequestMapping(value = "/addVersionInfo", method = RequestMethod.POST)
    public Result addAppVersionInfo(AppVersionInfo appVersionInfo) {
        return addNewAppVersion(appVersionInfo);
    }

    /**
     * 添加新版本
     * @param appVersionInfo
     * @return
     */
    private Result addNewAppVersion(AppVersionInfo appVersionInfo) {
        if (updateService.getAppVersionInfo(appVersionInfo.getVersionCode(), appVersionInfo.getAppKey()) != null) {
            return Result.error("该版本信息已存在！");
        } else {
            if (updateService.addAppVersionInfo(appVersionInfo)) {
                return Result.of(updateService.getAppVersionInfo(appVersionInfo.getVersionCode(), appVersionInfo.getAppKey()));
            } else {
                return Result.error("版本信息添加失败！");
            }
        }
    }

    /**
     * 上传apk文件
     *
     * @param file      apk文件
     * @param versionId apk的版本id
     * @return
     */
    @PreAuthorize("@el.check('newVersion:upload')")
    @PostMapping("/uploadApk")
    public Result uploadApkFile(MultipartFile file, String versionId) {
        try {
            QiniuContent qiniuContent = fileService.storeFile(file);
            if (ObjectUtil.isNotEmpty(qiniuContent)) {  //更新apk信息
                AppVersionInfo appVersionInfo = updateService.getAppVersionInfoById(versionId);
                updateVersionInfo(qiniuContent, appVersionInfo, BASE64DecodedMultipartFile.MultipartFileToFile(file));

                return Result.of(updateService.updateAppVersionInfo(appVersionInfo));
            } else {
                return Result.error("APK上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    private void updateVersionInfo(QiniuContent qiniuContent, AppVersionInfo appVersionInfo,File file) throws Exception {
        appVersionInfo.setApkMd5(Md5Utils.getFileMD5(file));
        appVersionInfo.setApkSize(FileUtils.getApkFileSize(file));
        appVersionInfo.setUploadTime(DateUtils.getNowString(DateUtils.yyyyMMddHHmmss.get()));
        appVersionInfo.setDownloadUrl(qiniuContent.getUrl());
    }

    @ResponseBody
    @RequestMapping(value = "/addAppVersion", method = RequestMethod.POST)
    public Result addAppVersion(MultipartFile file, AppVersionInfo appVersionInfo) {
        if (updateService.getAppVersionInfo(appVersionInfo.getVersionCode(), appVersionInfo.getAppKey()) != null) {
            return Result.error("该版本信息已存在！");
        } else {
            boolean result = updateService.addAppVersionInfo(appVersionInfo);
            if (result) {
                AppVersionInfo newVersion = updateService.getAppVersionInfo(appVersionInfo.getVersionCode(), appVersionInfo.getAppKey());
                try {
                    QiniuContent qiniuContent = fileService.storeFile(file);
                    if (ObjectUtil.isNotEmpty(qiniuContent)) {  //更新apk信息
                        updateVersionInfo(qiniuContent, newVersion,BASE64DecodedMultipartFile.MultipartFileToFile(file));

                        if (updateService.updateAppVersionInfo(newVersion)) {
                            return Result.of("版本信息添加成功!" );
                        } else {
                            return Result.error("Apk信息添加失败!");
                        }
                    } else {
                        return Result.error("APK上传失败:");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("APK上传失败:" + e.getMessage());
                }
            } else {
                return Result.error("版本信息添加失败！");
            }
        }
    }


    @GetMapping("/apk/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

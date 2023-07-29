package me.zhengjie.utils;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: XDF
 * @DateTime: 2023/7/29 11:08
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XUpdateResult {
    private Integer Code;
    private String Msg;
    private Integer UpdateStatus;
    private Integer VersionCode;
    private String VersionName;
    private String UploadTime;
    private String ModifyContent;
    private String DownloadUrl;
    private Integer ApkSize;
    private String ApkMd5;
}

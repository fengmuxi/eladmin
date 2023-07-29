package me.zhengjie.modules.system.domain;

import lombok.Data;

import javax.persistence.*;
@Entity
@Data
@Table(name = "app_version_info")
public class AppVersionInfo {
    @Id
    @Column(name = "version_id")
    private String versionId;

    @Column(name = "update_status")
    private Integer updateStatus;

    @Column(name = "version_code")
    private Integer versionCode;

    @Column(name = "version_name")
    private String versionName;

    @Column(name = "upload_time")
    private String uploadTime;

    @Column(name = "apk_size")
    private Integer apkSize;

    @Column(name = "app_key")
    private String appKey;

    @Column(name = "modify_content")
    private String modifyContent;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "apk_md5")
    private String apkMd5;
}
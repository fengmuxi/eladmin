package me.zhengjie.modules.system.repository;


import me.zhengjie.modules.system.domain.AppVersionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 86187
 */
public interface AppVersionInfoMapper extends JpaRepository<AppVersionInfo, String>, JpaSpecificationExecutor<AppVersionInfo> {

    @Query(nativeQuery = true,value = "select * from app_version_info where version_code = ?1 and app_key=?2 order by version_code desc ")
    List<AppVersionInfo> getAppVersionInfo(int versionCode, String appKey);

    @Query(nativeQuery = true,value = "select * from app_version_info where app_key=?1 order by version_code desc ")
    List<AppVersionInfo> getAppVersionInfoByKey(String appKey);

    @Query(nativeQuery = true,value = "select * from app_version_info where version_id=?1 ")
    AppVersionInfo getAppVersionInfoById(String versionId);
}
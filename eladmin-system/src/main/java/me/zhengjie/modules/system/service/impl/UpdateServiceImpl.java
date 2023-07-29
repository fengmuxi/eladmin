package me.zhengjie.modules.system.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.system.domain.PageData;
import me.zhengjie.modules.system.utils.DateUtils;
import org.springframework.stereotype.Service;
import me.zhengjie.modules.system.domain.AppVersionInfo;
import me.zhengjie.modules.system.repository.AppVersionInfoMapper;
import me.zhengjie.modules.system.service.UpdateService;

import java.util.List;

/**
 * 版本更新服务
 *
 * @author xuexiang
 * @since 2018/7/26 上午11:58
 */
@Service(value = "updateService")
@RequiredArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private final AppVersionInfoMapper appVersionInfoMapper;//这里会报错，但是并不会影响

    @Override
    public AppVersionInfo getLatestAppVersionInfo(int versionCode, String appKey) {
        List<AppVersionInfo> appInfos = getAllAppVersionInfo(appKey);

        if (appInfos.size() > 0) {
            AppVersionInfo appVersionInfo = appInfos.get(0); //获取到最新的版本
            if (appVersionInfo.getVersionCode() > versionCode) { //最新版本大，需要更新
                return appVersionInfo;
            } else {
                AppVersionInfo appInfo = new AppVersionInfo();
                appInfo.setUpdateStatus(UpdateService.NO_NEW_VERSION);
                return appInfo;
            }
        } else {
            AppVersionInfo appInfo = new AppVersionInfo();
            appInfo.setUpdateStatus(UpdateService.NO_NEW_VERSION);
            return appInfo;
        }
    }

    @Override
    public List<AppVersionInfo> getAllAppVersionInfo(String appKey) {
        return appVersionInfoMapper.getAppVersionInfoByKey(appKey);
//        return appVersionInfoMapper.findAll(new Specification<AppVersionInfo>() {
//            @Override
//            public Predicate toPredicate(Root<AppVersionInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> ps = new ArrayList<Predicate>();
//                ps.add(criteriaBuilder.equal(root.get("appKey"),appKey));
//                return query.where(ps.toArray(new Predicate[ps.size()])).getRestriction();
//            }
//        },Sort.by(Sort.Direction.DESC, "version_code"));
    }

    @Override
    public List<AppVersionInfo> getAllAppVersionInfo() {
        return appVersionInfoMapper.findAll();
    }

    /*
     * 这个方法中用到了我们开头配置依赖的分页插件PageHelper
     * 很简单，只需要在service层传入参数，然后将参数传递给一个插件的一个静态方法即可；
     * pageNum 开始页数
     * pageSize 每页显示的数据条数
     * */
    @Override
    public PageData<AppVersionInfo> getAllAppVersionInfo(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        PageData<AppVersionInfo> pageData = new PageData<>();
        Page<AppVersionInfo> page = PageHelper.startPage(pageNum, pageSize);
        pageData.setArray(appVersionInfoMapper.findAll());
        pageData.setPageNum(page.getPageNum())
                .setPageSize(page.getPageSize())
                .setTotal(page.getTotal());
        return pageData;
    }

    @Override
    public AppVersionInfo getAppVersionInfo(int versionCode, String appKey) {
        List<AppVersionInfo> list = appVersionInfoMapper.getAppVersionInfo(versionCode, appKey);
//        List<AppVersionInfo> list = appVersionInfoMapper.findAll(new Specification<AppVersionInfo>() {
//            @Override
//            public Predicate toPredicate(Root<AppVersionInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> ps = new ArrayList<Predicate>();
//                ps.add(criteriaBuilder.equal(root.get("appKey"),appKey));
//                ps.add(criteriaBuilder.equal(root.get("versionCode"),versionCode));
//                return query.where(ps.toArray(new Predicate[ps.size()])).getRestriction();
//            }
//        },Sort.by(Sort.Direction.DESC, "version_code"));
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean addAppVersionInfo(AppVersionInfo appVersionInfo) {
        appVersionInfo.setVersionId(IdUtil.getSnowflakeNextIdStr());
        appVersionInfo.setUploadTime(DateTime.now().toString());
        AppVersionInfo save = appVersionInfoMapper.save(appVersionInfo);
        return ObjectUtil.isNotEmpty(save);
    }

    @Override
    public boolean updateAppVersionInfo(AppVersionInfo appVersionInfo) {
        appVersionInfo.setUploadTime(DateTime.now().toString());
        AppVersionInfo save = appVersionInfoMapper.save(appVersionInfo);
        return ObjectUtil.isNotEmpty(save);
    }

    @Override
    public boolean deleteAppVersionInfo(String versionId) {
        appVersionInfoMapper.deleteById(versionId);
        return true;
    }

    @Override
    public AppVersionInfo getAppVersionInfoById(String versionId) {
        return appVersionInfoMapper.getAppVersionInfoById(versionId);
    }
}

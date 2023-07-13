/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import me.zhengjie.config.FileProperties;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.UserCacheManager;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.VerifyService;
import me.zhengjie.modules.system.service.dto.*;
import me.zhengjie.modules.system.service.mapstruct.UserLoginMapper;
import me.zhengjie.modules.system.service.mapstruct.UserMapper;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.*;
import me.zhengjie.utils.enums.CodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileProperties properties;
    private final RedisUtils redisUtils;
    private final UserCacheManager userCacheManager;
    private final OnlineUserService onlineUserService;
    private final UserLoginMapper userLoginMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerifyService verificationCodeService;
    private final EmailService emailService;
    @Value("${spring.sigIntegral}")
    private String sigIntegral;

    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    @Override
    public List<UserDto> queryAll(UserQueryCriteria criteria) {
        List<User> users = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return userMapper.toDto(users);
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(User resources) {
        if (userRepository.findByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }
        if (userRepository.findByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
//        if (userRepository.findByPhone(resources.getPhone()) != null) {
//            throw new EntityExistException(User.class, "phone", resources.getPhone());
//        }
        resources.setSigState("N");
        if (ObjectUtil.isEmpty(resources.getWallet())) {
            resources.setWallet(5);
        }
        resources.setVipTime(DateUtil.date());
        userRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User resources) throws Exception {
        User user = userRepository.findById(resources.getId()).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", resources.getId());
        User user1 = userRepository.findByUsername(resources.getUsername());
        User user2 = userRepository.findByEmail(resources.getEmail());
//        User user3 = userRepository.findByPhone(resources.getPhone());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }
        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
//        if (user3 != null && !user.getId().equals(user3.getId())) {
//            throw new EntityExistException(User.class, "phone", resources.getPhone());
//        }
        // 如果用户的角色改变
        if (!resources.getRoles().equals(user.getRoles())) {
            redisUtils.del(CacheKey.DATA_USER + resources.getId());
            redisUtils.del(CacheKey.MENU_USER + resources.getId());
            redisUtils.del(CacheKey.ROLE_AUTH + resources.getId());
        }
        // 修改部门会影响 数据权限
        if (!Objects.equals(resources.getDept(),user.getDept())) {
            redisUtils.del(CacheKey.DATA_USER + resources.getId());
        }
        // 如果用户被禁用，则清除用户登录信息
        if(!resources.getEnabled()){
            onlineUserService.kickOutForUsername(resources.getUsername());
        }
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setDept(resources.getDept());
        user.setJobs(resources.getJobs());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());
        user.setVipTime(resources.getVipTime());
        user.setWallet(resources.getWallet());
        if (StrUtil.isNotEmpty(resources.getPassword())) {
            user.setPassword(passwordEncoder.encode(resources.getPassword()));
        }
        userRepository.save(user);
        // 清除缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(User resources) {
        User user = userRepository.findById(resources.getId()).orElseGet(User::new);
        User user1 = userRepository.findByPhone(resources.getPhone());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setGender(resources.getGender());
        userRepository.save(user);
        // 清理缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSigSate() {
        userRepository.updateSigSate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> sig() {
        String uuid = UUID.randomUUID().toString().replace("_", "");
        try {
            boolean isLock = redisUtils.set("lock:sig:" + SecurityUtils.getCurrentUserId(), uuid, 30, TimeUnit.SECONDS);
            if (isLock) {
                User byId = userRepository.getById(SecurityUtils.getCurrentUserId());
                if ("Y".equals(byId.getSigState())) {
                    return Result.error("请勿重复签到！");
                }
                Integer sig = userRepository.sig(SecurityUtils.getCurrentUserId(),sigIntegral);
                if (sig>0){
                    // 清理缓存
                    delCaches(SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUsername());
                    return Result.of("签到成功！");
                }
                return Result.error("签到失败！");
            }else {
                return Result.error("请勿点击过快！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("内部错误请联系管理员！");
        }finally {
            String  lock= redisUtils.get("lock:sig:" + SecurityUtils.getCurrentUserId()).toString();
            if (lock.equals(uuid)) {
                redisUtils.del("lock:sig:" + SecurityUtils.getCurrentUserId());
            }
        }
    }

    @Override
    public Result<Object> isVip() {
        Boolean isVip = redisUtils.hasKey("vip:" + SecurityUtils.getCurrentUserId());
        if (Boolean.TRUE.equals(isVip)) {
            String vip = redisUtils.get("vip:" + SecurityUtils.getCurrentUserId()).toString();
            return Result.of(vip);
        }
        User byId = userRepository.getById(SecurityUtils.getCurrentUserId());
        if (DateUtil.compare(byId.getVipTime(),DateUtil.date())>0) {
            // 判断当前时间与过期时间的时间差
            long differ = byId.getVipTime().getTime() - System.currentTimeMillis();
            redisUtils.set("vip:" + SecurityUtils.getCurrentUserId(), "会员", differ, TimeUnit.MILLISECONDS);
            return Result.of("会员");
        }
        redisUtils.set("vip:" + SecurityUtils.getCurrentUserId(), "非会员");
        return Result.error("非会员");
    }

    @Override
    public Result<Object> registerUser(User user) {
        try {
            if (userRepository.findByUsername(user.getUsername()) != null) {
                throw new EntityExistException(User.class, "username", user.getUsername());
            }
            if (userRepository.findByEmail(user.getEmail()) != null) {
                throw new EntityExistException(User.class, "email", user.getEmail());
            }
            user.setVipTime(DateUtil.date());
            user.setWallet(5);
            user.setSigState("N");
            userRepository.save(user);
            return Result.of("注册成功！");
        } catch (EntityExistException e) {
            e.printStackTrace();
            return Result.error("注册失败！");
        }
    }

    @Override
    public Result<Object> setWallet(Integer number) {
        try {
            User byId = userRepository.getById(SecurityUtils.getCurrentUserId());
            if (byId.getWallet()-number<0) {
                return Result.error("余额不足！");
            }
            byId.setWallet(byId.getWallet()-number);
            userRepository.save(CopyUtil.copy(byId,User.class));
            return Result.of("修改成功！");
        } catch (Exception e) {
            return Result.error("异常！");
        }
    }

    @Override
    public Result<Object> myCenter(User resources) {
        try {
            User byId = userRepository.getById(SecurityUtils.getCurrentUserId());
            if (StrUtil.isNotEmpty(resources.getNickName())) {
                byId.setNickName(resources.getNickName());
            }
            userRepository.save(byId);
            return Result.of("修改成功！");
        } catch (Exception e) {
            return Result.error("异常！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> restPwd(String mail, String code) {
        try {
            User user = userRepository.findByEmail(mail);
            verificationCodeService.validated(CodeEnum.EMAIL_RESET_PWD_CODE.getKey() + user.getEmail(), code);
            String pwd = RandomUtil.randomString(6);
            EmailVo emailVo = verificationCodeService.sendEmailCode(user.getEmail(), CodeEnum.EMAIL_RESET_PWD.getKey(), CodeEnum.EMAIL_RESET_PWD.getDescription(),pwd);
            emailService.send(emailVo,emailService.find());
            user.setPassword(passwordEncoder.encode(pwd));
            update(user);
            redisUtils.del(CodeEnum.EMAIL_RESET_PWD_CODE.getKey() + user.getEmail());
            return Result.of("重置成功！新密码已发送至您的邮箱!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("重置失败！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateLoginTime(String username) {
        Integer integer = userRepository.updateLoginTime(username, me.zhengjie.utils.DateUtil.getNowDate());
        if (integer>0) {
            return Result.of();
        }
        return Result.error();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailTitle(EmailVo emailVo) {
        EmailConfig emailConfig=emailService.find();
        if(emailConfig.getId() == null){
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
        String user = emailConfig.getFromUser().split("@")[0];
        account.setUser(user);
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser()+"<"+emailConfig.getFromUser()+">");
        // ssl方式发送
        account.setSslEnable(true);
        //指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
        account.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        //如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
        account.setSocketFactoryFallback(true);
        // 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口456
        account.setSocketFactoryPort(465);
        // 解决(Could not connect to SMTP host:smtp.exmail.qq.com,port:465)
        account.setSslProtocols("TLSv1.2");
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        String content = emailVo.getContent();
        List<User> all = userRepository.findAll();
        List<String> emails=new ArrayList();
        for (int i = 0; i < all.size(); i++) {
            emails.add(all.get(i).getEmail());
        }
        emailVo.setTos(emails);
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            // 清理缓存
            UserDto user = findById(id);
            delCaches(user.getId(), user.getUsername());
        }
        userRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Cacheable(key = "'userInfo:' + #p0")
    public UserDto findByName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }

    @Override
    public UserLoginDto getLoginData(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userLoginMapper.toDto(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String pass) {
        userRepository.updatePass(username, pass, new Date());
        flushCache(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        // 文件大小验证
        FileUtil.checkSize(properties.getAvatarMaxSize(), multipartFile.getSize());
        // 验证文件上传的格式
        String image = "gif jpg png jpeg";
        String fileType = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        if(fileType != null && !image.contains(fileType)){
            throw new BadRequestException("文件格式错误！, 仅支持 " + image +" 格式");
        }
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        userRepository.save(user);
        if (StringUtils.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUsername();
        flushCache(username);
        return new HashMap<String, String>(1) {{
            put("avatar", file.getName());
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username, email);
        flushCache(username);
    }

    @Override
    public void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDto userDTO : queryAll) {
            List<String> roles = userDTO.getRoles().stream().map(RoleSmallDto::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDTO.getUsername());
            map.put("角色", roles);
            map.put("部门", userDTO.getDept().getName());
            map.put("岗位", userDTO.getJobs().stream().map(JobSmallDto::getName).collect(Collectors.toList()));
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("修改密码的时间", userDTO.getPwdResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        redisUtils.del("user::userInfo:" + username);
        redisUtils.del("vip:" + id);
        flushCache(username);
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username /
     */
    private void flushCache(String username) {
        userCacheManager.cleanUserCache(username);
    }
}

package com.demo.community.service;

import com.demo.community.dto.AccessTokenDTO;
import com.demo.community.dto.AuthUserDTO;
import com.demo.community.dto.GithubUser;
import com.demo.community.dto.ResultDTO;
import com.demo.community.enums.AuthUserTypeEnum;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.mapper.AuthUserMapper;
import com.demo.community.mapper.UserMapper;
import com.demo.community.model.AuthUser;
import com.demo.community.model.AuthUserExample;
import com.demo.community.model.User;
import com.demo.community.model.UserExample;
import com.demo.community.provider.AliyunProbvider;
import com.demo.community.provider.GithubProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthUserService {

    private static final Integer LOGIN_COOKIE_TIME = 7 * 24 * 60 * 60;

    private static final Integer RANDOM_USERNAME_LENGTH = 11;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Autowired
    private AuthUserMapper authUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;

    public ResultDTO userNameRegister(AuthUserDTO authUserDTO, HttpServletResponse response) {

        UserExample example = new UserExample();
        example.createCriteria()
                .andNameEqualTo(authUserDTO.getUserName());
        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {

            //注册 USER 表
            String token = UUID.randomUUID().toString();
            User user = new User();
            user.setName(authUserDTO.getUserName());
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtCreate(System.currentTimeMillis());
            userMapper.insert(user);

            Cookie user_session = new Cookie("user_session", token);
            user_session.setMaxAge(LOGIN_COOKIE_TIME);
            response.addCookie(user_session);

            //注册 AUTH_USER 表
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andNameEqualTo(user.getName());
            User findUser = userMapper.selectByExample(userExample).get(0);
            insertAuthUser(findUser.getId(), AuthUserTypeEnum.USERNAME_PASSWORD.getType(), findUser.getName(), authUserDTO.getPassword());
            return ResultDTO.okOf();
        } else {
            return ResultDTO.errorOf(CustomizeErrorCode.USERNAME_IS_EXIST);
        }
    }

    private void insertAuthUser(Long userId, Integer identifyType, String identifier, String credential) {

        AuthUser authUser = new AuthUser();
        authUser.setCredential(credential);
        authUser.setUserId(userId);
        authUser.setIdentifier(identifier);
        authUser.setIdentityType(identifyType);
        authUserMapper.insert(authUser);
    }

    public ResultDTO loginByUserName(AuthUserDTO authUserDTO, HttpServletResponse response) {

        //验证密码
        AuthUserExample userExample = new AuthUserExample();
        userExample.createCriteria()
                .andIdentifierEqualTo(authUserDTO.getUserName())
                .andIdentityTypeEqualTo(AuthUserTypeEnum.USERNAME_PASSWORD.getType());
        AuthUser authUser = authUserMapper.selectByExample(userExample).get(0);
        if (authUser == null)
            return ResultDTO.errorOf(CustomizeErrorCode.USER_IS_NOT_EXIST);
        if (!authUser.getCredential().equals(authUserDTO.getPassword()))
            return ResultDTO.errorOf(CustomizeErrorCode.PASSWORD_IS_WRONG);

        //验证成功，登录
        User user = userMapper.selectByPrimaryKey(authUser.getUserId());
        Cookie user_session = new Cookie("user_session", user.getToken());
        user_session.setMaxAge(LOGIN_COOKIE_TIME);

        //传递 cookie 和 session
        response.addCookie(user_session);
        return ResultDTO.okOf();
    }

    public void authByGithub(HttpServletResponse response, String state, String code) {

        AccessTokenDTO accessTokenDTO = createAccessTokenDTO(state, code, githubRedirectUri, githubClientId, githubClientSecret);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);

        //验证
        AuthUserExample example = new AuthUserExample();
        example.createCriteria()
                .andIdentityTypeEqualTo(AuthUserTypeEnum.GITHUB.getType())
                .andIdentifierEqualTo(githubUser.getId().toString());
        List<AuthUser> authUsers = authUserMapper.selectByExample(example);

        //创建用户
        if (authUsers.size() == 0) {

            User user = new User();
            String userName = "用户：" + getStringRandom(RANDOM_USERNAME_LENGTH);
            user.setName(userName);
            if (StringUtils.isNotBlank(githubUser.getBio()))
                user.setBio(githubUser.getBio());

            if (StringUtils.isNotBlank(githubUser.getAvatar_url())) {
                user.setAvatarUrl(githubUser.getAvatar_url());
            }

            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userMapper.insert(user);

            //通过随机生成的 username 进行查询
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andNameEqualTo(userName);
            User dbUser = userMapper.selectByExample(userExample).get(0);

            AuthUser authUser = new AuthUser();
            authUser.setIdentityType(AuthUserTypeEnum.GITHUB.getType());
            authUser.setUserId(dbUser.getId());
            authUser.setIdentifier(String.valueOf(githubUser.getId()));
            authUser.setCredential(accessToken);

            authUserMapper.insert(authUser);

            Cookie user_session = new Cookie("user_session", token);
            user_session.setMaxAge(LOGIN_COOKIE_TIME);
            response.addCookie(user_session);
        } else {

            AuthUser authUser = authUsers.get(0);

            //通过 userId 进行查询
            String token = UUID.randomUUID().toString();
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andIdEqualTo(authUser.getUserId());
            User dbUser = userMapper.selectByExample(userExample).get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setToken(token);

            UserExample updateUserExample = new UserExample();
            updateUserExample.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, updateUserExample);

            Cookie user_session = new Cookie("user_session", token);
            user_session.setMaxAge(LOGIN_COOKIE_TIME);
            response.addCookie(user_session);
        }
    }

    private String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            //输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "Num";
            //输出大写、 还是小写字母
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    private AccessTokenDTO createAccessTokenDTO(String state, String code, String githubRedirectUri, String
            githubClientId, String githubClientSecret) {
        AccessTokenDTO createAccessTokenDTO = new AccessTokenDTO();
        createAccessTokenDTO.setCode(code);
        createAccessTokenDTO.setState(state);
        createAccessTokenDTO.setRedirect_uri(githubRedirectUri);
        createAccessTokenDTO.setClient_id(githubClientId);
        createAccessTokenDTO.setClient_secret(githubClientSecret);
        return createAccessTokenDTO;
    }
}
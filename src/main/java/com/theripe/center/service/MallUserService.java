package com.theripe.center.service;

import com.theripe.center.bean.MallUser;
import com.theripe.center.controller.vo.MallUserVO;
import com.theripe.center.utils.PageQueryUtil;
import com.theripe.center.utils.PageResult;

import javax.servlet.http.HttpSession;

/**
 * @Author TheRipe
 * @create 2021/7/19 16:52
 */
public interface MallUserService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getMallUsersPage(PageQueryUtil pageUtil);

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * 登录
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /**
     * 用户信息修改并返回最新的用户信息

     */
     MallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Integer[] ids, int lockStatus);
}

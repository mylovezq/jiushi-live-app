package top.mylove7.live.api.live.service;

import top.mylove7.live.api.live.vo.HomePageVO;


/**
 * @Author jiushi
 *
 * @Description
 */
public interface IHomePageService {


    /**
     * 初始化页面获取的信息
     *
     * @param userId
     * @return
     */
    HomePageVO initPage(Long userId);


}

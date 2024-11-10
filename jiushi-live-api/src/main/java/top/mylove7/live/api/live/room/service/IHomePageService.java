package top.mylove7.live.api.live.room.service;

import top.mylove7.live.api.live.room.vo.HomePageVO;


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

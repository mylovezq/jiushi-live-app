package top.mylove7.live.living.provider.room.rpc;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.javafaker.Faker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.mylove7.live.common.interfaces.dto.PageWrapper;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.im.core.server.interfaces.dto.ImOfflineDTO;
import top.mylove7.live.im.core.server.interfaces.dto.ImOnlineDTO;
import top.mylove7.live.living.provider.room.dao.po.DoctorInfo;
import top.mylove7.live.living.interfaces.room.dto.LivingPkRespDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.living.provider.room.dao.mapper.MyDataMapper;
import top.mylove7.live.living.provider.room.dao.po.MyDataPO;
import top.mylove7.live.living.provider.room.service.ILivingRoomService;
import top.mylove7.live.living.provider.room.service.ILivingRoomTxService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
@Slf4j
public class LivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private MyDataMapper myDataMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Resource
    private ILivingRoomTxService livingRoomTxService;

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.queryUserIdByRoomId(livingRoomReqDTO);
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.list(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Long roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }


    @Override
    public Long startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomTxService.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.onlinePk(livingRoomReqDTO);
    }

    @Override
    public Long queryOnlinePkUserId(Long roomId) {
        return livingRoomService.queryOnlinePkUserId(roomId);
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.offlinePk(livingRoomReqDTO);
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        livingRoomService.userOnlineHandler(imOnlineDTO);
    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        livingRoomService.userOfflineHandler(imOfflineDTO);
    }

    @Override
    public LivingRoomRespDTO queryByAuthorId(Long userId) {
        return livingRoomService.queryByAuthorId(userId);
    }

    @Override
    @Transactional(rollbackFor =Exception.class)
    public void initInfo() {

        Faker faker = new Faker();
        Random random = new Random();
//        IntStream.range(0, 1000).parallel().forEach(i -> {
//            log.info("开始" + i + "条数据");
//
//            List<DoctorInfo> myDataPOList = IntStream.range(0, 1000).mapToObj(index -> {
//                long id = IdWorker.getId();
//                return   new DoctorInfo(id, faker, random);
//            }).collect(Collectors.toList());
//            mongoTemplate.insert(myDataPOList, DoctorInfo.class);
//            log.info("结束" + i + "条数据");
//        });
        long id = IdWorker.getId();
        List<DoctorInfo> myDataPOList = new ArrayList<>();
        myDataPOList.add(new DoctorInfo(id, faker, random));
        mongoTemplate.insert(myDataPOList, DoctorInfo.class);
        mongoTemplate.insert(myDataPOList, DoctorInfo.class);
        throw new BizErrorException("66");

    }
}

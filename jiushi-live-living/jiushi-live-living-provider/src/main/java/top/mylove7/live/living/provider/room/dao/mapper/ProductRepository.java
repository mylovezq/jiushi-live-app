package top.mylove7.live.living.provider.room.dao.mapper;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.mylove7.live.living.provider.room.dao.po.EsDoctorInfo;

public interface ProductRepository extends ElasticsearchRepository<EsDoctorInfo, Long> {

}
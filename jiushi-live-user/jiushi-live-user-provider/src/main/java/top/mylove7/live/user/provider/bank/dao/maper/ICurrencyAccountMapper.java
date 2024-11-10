package top.mylove7.live.user.provider.bank.dao.maper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.mylove7.live.user.provider.bank.dao.po.CurrencyAccountPO;

/**
 * 虚拟币账户mapper
 *
 * @Author jiushi
 *
 * @Description
 */
@Mapper
public interface ICurrencyAccountMapper extends BaseMapper<CurrencyAccountPO> {

    @Update("update t_currency_account set current_balance = current_balance + #{num}, total_charged = total_charged + #{num}  where user_id = #{userId} and status = 1")
    boolean incr(@Param("userId") Long userId,@Param("num") Long num);

    @Select("select current_balance from t_currency_account where user_id=#{userId} and status = 1 limit 1")
    Integer queryBalance(@Param("userId") Long userId);

    @Update("update t_currency_account set current_balance = current_balance - #{num} where user_id = #{userId} and current_balance >= #{num} and status = 1")
    boolean decr(@Param("userId") Long userId,@Param("num") Long num);


}

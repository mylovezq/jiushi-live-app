package top.mylove7.live.common.interfaces.dto;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImUserInfoTokenDto {
    private Long userId;
    private Long appId;
    private Long roomId;

    public ImUserInfoTokenDto(Long userId, Long appId){
        this.userId = userId;
        this.appId = appId;
    }

    public String toJson(){
        return JSONUtil.toJsonStr(this);
    }

    public static ImUserInfoTokenDto fromJson(String json){
        if(json == null || json.isEmpty()){
            return null;
        }
        return JSONUtil.toBean(json,ImUserInfoTokenDto.class);
    }
}

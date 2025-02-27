package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 18:01
 */
@Data
public class UpdateStockNumDto {
    private boolean isSuccess;
    private boolean isEmptyStock;

    public UpdateStockNumDto(boolean isSuccess, boolean isEmptyStock) {
        this.isSuccess = isSuccess;
        this.isEmptyStock = isEmptyStock;
    }
    public UpdateStockNumDto() {
    }
}

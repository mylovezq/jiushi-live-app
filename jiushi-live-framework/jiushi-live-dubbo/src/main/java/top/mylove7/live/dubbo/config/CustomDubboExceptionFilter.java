package top.mylove7.live.dubbo.config;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import top.mylove7.live.common.interfaces.error.BizErrorException;

@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class CustomDubboExceptionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 调用实际的方法
            return invoker.invoke(invocation);
        } catch (Throwable t) {

            if (t instanceof BizErrorException){
                throw t;
            }
           throw t;
        }
    }


}

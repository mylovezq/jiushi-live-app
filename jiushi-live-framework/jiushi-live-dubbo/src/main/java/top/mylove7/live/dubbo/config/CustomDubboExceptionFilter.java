package top.mylove7.live.dubbo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import top.mylove7.live.common.interfaces.error.BizErrorException;

@Slf4j
@Activate(group = CommonConstants.PROVIDER)
public class CustomDubboExceptionFilter implements Filter, Filter.Listener {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            log.error("\ndubbo服务端服务端主机【{}】\n接口名称【{}】\n方法名称【{}】\n调用者【{}】\n异常信息"
                    ,RpcContext.getServiceContext().getLocalAddress().getHostName()+":" +RpcContext.getServiceContext().getLocalAddress().getPort()
                    ,invoker.getInterface().getName(),
                    invocation.getMethodName(),
                    RpcContext.getServiceContext().getRemoteApplicationName()
            ,appResponse.getException());
        }
    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
        log.error("5-36", "", "", "Got unchecked and undeclared exception which called by " + RpcContext.getServiceContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);

    }


}
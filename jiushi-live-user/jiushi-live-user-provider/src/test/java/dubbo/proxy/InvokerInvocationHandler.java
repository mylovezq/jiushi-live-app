package dubbo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author jiushi
 *
 * @Description
 */
public class InvokerInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("getUserInfo")) {
            return args[0] + "-test";
        }
        return "no match";
    }
}

package top.mylove7.live.im.core.server.starter;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.mylove7.live.im.core.server.common.ChannelHandlerContextCache;
import top.mylove7.live.im.core.server.common.TcpImMsgDecoder;
import top.mylove7.live.im.core.server.common.TcpImMsgEncoder;
import top.mylove7.live.im.core.server.handler.tcp.TcpImServerCoreHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static top.mylove7.live.common.interfaces.utils.ExecutorConfig.IO_EXECUTOR;

/**
 * @Author jiushi
 *
 * @Description
 */
@Configuration
@Slf4j
public class TcpNettyImServerStarter implements InitializingBean {

    //指定监听的端口
    @Value("${jiushi.im.tcp.port}")
    private int port;
    @Resource
    private TcpImServerCoreHandler imServerCoreHandler;
    @Resource
    private Environment environment;

    //基于netty去启动一个java进程，绑定监听的端口

    public void startApplication() throws UnknownHostException, InterruptedException {
        //处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        bootstrap.childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                //打印日志，方便观察
                log.info("初始化连接渠道");
                //设计消息体
                //增加编解码器
                ch.pipeline().addLast(new TcpImMsgDecoder());
                ch.pipeline().addLast(new TcpImMsgEncoder());
                ch.pipeline().addLast(imServerCoreHandler);
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        this.setIpAndPort();
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        log.info("服务启动成功，监听端口为{}", port);
        //这里会阻塞掉主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    private void setIpAndPort() throws UnknownHostException {
        //获取im的服务注册ip和暴露端口
        String registryIpEnv = environment.getProperty("DUBBO_IP_TO_REGISTRY");
        if (StrUtil.isBlank(registryIpEnv)){

            registryIpEnv = NetUtil.getLocalhostStr();
        }
        String registryPortEnv = environment.getProperty("DUBBO_PORT_TO_REGISTRY");

        if (StrUtil.isBlank(registryPortEnv)){
            registryPortEnv  = environment.getProperty("dubbo.protocol.port");;
        }
        if (StringUtils.isEmpty(registryPortEnv) || StringUtils.isEmpty(registryIpEnv)) {
            throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
        }
        ChannelHandlerContextCache.setServerIpAddress(registryIpEnv + ":" + registryPortEnv);
        log.info("注册的ip{}和端口{}", registryIpEnv, registryPortEnv);
    }

    @Override
    public void afterPropertiesSet() {

        IO_EXECUTOR.execute(() -> {
            Thread.currentThread().setName("jiushi-live-im-server-tcp");
            try {
                startApplication();
            } catch (Exception e) {
               log.error("jiushi-live-im-server-tcp启动失败",e);
               System.exit(-1);
            }

        });



    }
}

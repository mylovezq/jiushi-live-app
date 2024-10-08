package org.qiyu.live.im.core.server.starter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.WebsocketEncoder;
import org.qiyu.live.im.core.server.handler.ws.WsImServerCoreHandler;
import org.qiyu.live.im.core.server.handler.ws.WsSharkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @Author jiushi
 *
 * @Description
 */
@Configuration
public class WsNettyImServerStarter implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(WsNettyImServerStarter.class);

    //指定监听的端口
    @Value("${jiushi.im.ws.port}")
    private int wsPort;
    @Resource
    private WsSharkHandler wsSharkHandler;
    @Resource
    private WsImServerCoreHandler wsImServerCoreHandler;
    @Resource
    private Environment environment;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    //基于netty去启动一个java进程，绑定监听的端口
    public void startApplication() throws Exception {
        //处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                //打印日志，方便观察
                LOGGER.info("初始化连接渠道");
                //因为基于http协议 使用http的编码和解码器
                ch.pipeline().addLast(new HttpServerCodec());
                //是以块方式写 添加处理器
                ch.pipeline().addLast(new ChunkedWriteHandler());
                //http数据在传输过程中是分段 就是可以将多个段聚合 这就是为什么当浏览器发生大量数据时 就会发生多次http请求
                ch.pipeline().addLast(new HttpObjectAggregator(81920));
                ch.pipeline().addLast(new WebsocketEncoder());
                ch.pipeline().addLast(wsSharkHandler);
                ch.pipeline().addLast(wsImServerCoreHandler);
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        //获取im的服务注册ip和暴露端口
        this.setDubboIpAndPort();
        this.registerNacosService();
        ChannelFuture channelFuture = bootstrap.bind(wsPort).sync();
        LOGGER.info("im webSocket服务启动成功，监听端口为{}", wsPort);
        //这里会阻塞掉主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    private void registerNacosService() throws Exception {
        String applicationName  = environment.getProperty("spring.application.name");
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
        properties.setProperty(PropertyKeyConst.NAMESPACE, nacosDiscoveryProperties.getNamespace());
        NamingService namingService = NamingFactory.createNamingService(properties);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        namingService.registerInstance(applicationName + "-ws",hostAddress, wsPort);
        ChannelHandlerContextCache.setWsIpAddress(hostAddress + ":" + wsPort);

    }

    private void setDubboIpAndPort() throws UnknownHostException {
        //获取im的服务注册ip和暴露端口
        String registryIpEnv = environment.getProperty("DUBBO_IP_TO_REGISTRY");
        if (StrUtil.isBlank(registryIpEnv)){
            InetAddress localHost = InetAddress.getLocalHost();
            registryIpEnv = localHost.getHostAddress();
        }
        String registryPortEnv = environment.getProperty("DUBBO_PORT_TO_REGISTRY");

        if (StrUtil.isBlank(registryPortEnv)){
            registryPortEnv  = environment.getProperty("dubbo.protocol.port");;
        }
        if (StringUtils.isEmpty(registryPortEnv) || StringUtils.isEmpty(registryIpEnv)) {
            throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
        }
        ChannelHandlerContextCache.setServerIpAddress(registryIpEnv + ":" + registryPortEnv);
        LOGGER.info("duboo注册的ip{}和端口{}", registryIpEnv, registryPortEnv);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread nettyServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startApplication();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        nettyServerThread.setName("jiushi-live-im-server-ws");
        nettyServerThread.start();
    }
}

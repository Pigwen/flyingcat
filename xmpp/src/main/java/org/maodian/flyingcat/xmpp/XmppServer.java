/*
 * Copyright 2013 - 2013 Cole Wen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.maodian.flyingcat.xmpp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ServiceLoader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Cole Wen
 *
 */
public class XmppServer {
  
  private final int port;
  
  private XmppServer(int port) {
    this.port = port;
  }

  private void run(org.springframework.context.ApplicationContext springCtx) throws InterruptedException {
    ServerBootstrap b = new ServerBootstrap();
    try {
      b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
      .channel(NioServerSocketChannel.class)
      .localAddress(port)
      .childHandler(new XmppServerInitializer(springCtx)) 
      .option(ChannelOption.TCP_NODELAY, true)
      .option(ChannelOption.SO_KEEPALIVE, true);

      b.bind().sync().channel().closeFuture().sync();
    } finally {
      b.shutdown();
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    int port;
    if (args.length > 0) {
        port = Integer.parseInt(args[0]);
    } else {
        port = 5222;
    }
    org.springframework.context.ApplicationContext springCtx = new ClassPathXmlApplicationContext("beans.xml", "shiro.xml");
    
    
/*    SecurityManager securityManager = injector.getInstance(SecurityManager.class);
    SecurityUtils.setSecurityManager(securityManager);*/
    new XmppServer(port).preRun(springCtx).run(springCtx);
  }

  private XmppServer preRun(org.springframework.context.ApplicationContext springCtx) {
    ApplicationContext ctx = springCtx.getBean(ApplicationContext.class);
    ServiceLoader<Extension> extLoader = ServiceLoader.load(Extension.class);
    for (Extension ext : extLoader) {
      ext.setInjector(springCtx);
      ext.register(ctx);
    }
    return this;
  }
}

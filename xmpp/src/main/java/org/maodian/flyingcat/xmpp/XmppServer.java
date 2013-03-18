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

import org.springframework.context.ApplicationContext;
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

  private void run(ApplicationContext beanFactory) throws InterruptedException {
    ServerBootstrap b = new ServerBootstrap();
    try {
      b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
      .channel(NioServerSocketChannel.class)
      .localAddress(port)
      .childHandler(new XmppServerInitializer(beanFactory)) 
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
    ApplicationContext beanFactory = new ClassPathXmlApplicationContext("beans.xml", "shiro.xml");
    new XmppServer(port).preRun(beanFactory).run(beanFactory);
  }

  private XmppServer preRun(ApplicationContext beanFactory) {
    GlobalContext ctx = beanFactory.getBean(GlobalContext.class);
    ServiceLoader<Extension> extLoader = ServiceLoader.load(Extension.class);
    for (Extension ext : extLoader) {
      ext.setBeanFactory(beanFactory);
      ext.register(ctx);
    }
    return this;
  }
}

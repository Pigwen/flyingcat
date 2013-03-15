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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;
import org.maodian.flyingcat.di.XmppModule;
import org.maodian.flyingcat.di.XmppShiroModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Cole Wen
 *
 */
public class XmppServer {
  
  private final int port;
  
  private XmppServer(int port) {
    this.port = port;
  }

  private void run(Injector injector) throws InterruptedException {
    ServerBootstrap b = new ServerBootstrap();
    try {
      b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
      .channel(NioServerSocketChannel.class)
      .localAddress(port)
      .childHandler(new XmppServerInitializer(injector)) 
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
    Injector injector = Guice.createInjector(new XmppModule(), new XmppShiroModule(), new ShiroAopModule());
    
    SecurityManager securityManager = injector.getInstance(SecurityManager.class);
    SecurityUtils.setSecurityManager(securityManager);
    new XmppServer(port).preRun(injector).run(injector);
  }

  private XmppServer preRun(Injector injector) {
    ApplicationContext ctx = injector.getInstance(ApplicationContext.class);
    ctx.init();
    ServiceLoader<Extension> extLoader = ServiceLoader.load(Extension.class);
    for (Extension ext : extLoader) {
      ext.setInjector(injector);
      ext.register(ctx);
    }
    return this;
  }
}

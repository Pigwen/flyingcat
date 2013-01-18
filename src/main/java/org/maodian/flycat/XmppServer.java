/**
 * 
 */
package org.maodian.flycat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Cole Wen
 *
 */
public class XmppServer {
  
  private final int port;
  
  private XmppServer(int port) {
    this.port = port;
  }

  private void run() throws InterruptedException {
    ServerBootstrap b = new ServerBootstrap();
    try {
      b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
       .channel(NioServerSocketChannel.class)
       .localAddress(port)
       .childHandler(new XmppServerInitializer());

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
    new XmppServer(port).run();
  }

}

package com.example.netty.handlers;

/**
 * Created by Krisztian on 2016. 10. 31..
 */

import com.example.netty.repositories.ChannelRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Created by Krisztian on 2016. 10. 31..
 */
@Component
@Qualifier("tcpServerHandler")
@ChannelHandler.Sharable
public class TCPServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ChannelRepository channelRepository;

    private static Logger logger = Logger.getLogger(TCPServerHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");

        ctx.fireChannelActive();
        logger.debug(ctx.channel().remoteAddress());
        String channelKey = ctx.channel().remoteAddress().toString();
        channelRepository.put(channelKey, ctx.channel());

        ctx.writeAndFlush("Your channel key is " + channelKey + "\n\r");

        logger.debug("Binded Channel Count is " + this.channelRepository.size());
        System.out.println("binded: "+this.channelRepository.size());

        Set<String> connectedChannels = getChannelRepository().getChannelCache().keySet();
        for(String entry : connectedChannels){
            System.out.println("Channel: "+entry);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String stringMessage = (String) msg;
        String channelKey = ctx.channel().remoteAddress().toString();
        logger.debug(stringMessage);
        System.out.println(channelKey+" message: "+stringMessage);
        String[] splitMessage = stringMessage.split("::");

        if ( splitMessage.length != 2 ) {
            ctx.channel().writeAndFlush(stringMessage + "\n\r");
            return;
        }

        if ( channelRepository.get(splitMessage[0]) != null ) {
            channelRepository.get(splitMessage[0]).writeAndFlush(splitMessage[1] + "\n\r");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                logger.debug("Reader Idle");
                ctx.writeAndFlush("Close connection because idle state.");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("PING");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx);
        System.out.println("inactive");
        String channelKey = ctx.channel().remoteAddress().toString();
        this.channelRepository.remove(channelKey);

        logger.debug("Binded Channel Count is " + this.channelRepository.size());
    }

    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public ChannelRepository getChannelRepository() {
        return channelRepository;
    }
}

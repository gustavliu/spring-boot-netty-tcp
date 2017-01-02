package com.example.services;

import com.example.netty.repositories.ChannelRepository;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by Krisztian on 2017. 01. 02..
 */
@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    public Channel getChannel(String key){
        return channelRepository.get(key);
    }

    public Set<String> getChannelNames(){
        return channelRepository.getChannelCache().keySet();
    }
}

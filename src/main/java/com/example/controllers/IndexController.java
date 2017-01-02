package com.example.controllers;

import com.example.netty.repositories.ChannelRepository;
import com.example.services.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

/**
 * Created by Krisztian on 2016. 12. 31..
 */
@Controller
public class IndexController {

    @Autowired
    ChannelService channelService;

    @RequestMapping("/")
    String index(Model model) {
        System.out.println("index");
        model.addAttribute("channels",channelService.getChannelNames());
        return "index";
    }
}

package com.eryansky.modules.fop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 页面跳转
 *
 * @author yudian-it
 * @date 2017/12/27
 */
@Controller()
@RequestMapping(value = "/fop")
public class FopIndexController {

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String go2Index() {
        return "index";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String root() {
        System.out.println("v");
        return "redirect:/fop/index";
    }

    @RequestMapping(value = "fop", method = RequestMethod.GET)
    public String fop() {
        System.out.println("fop");
        return "fop/fop";
    }
}

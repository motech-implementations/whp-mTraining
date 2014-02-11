package org.motechproject.mtraining.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/mtraining")
public class PingController {    // service methods for angular ui

    @RequestMapping("/ping")
    @ResponseBody
    public String ping() {
        return "Motech MTraining Ping Page";
    }
}

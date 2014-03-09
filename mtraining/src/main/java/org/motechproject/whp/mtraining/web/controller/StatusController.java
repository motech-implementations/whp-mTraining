package org.motechproject.whp.mtraining.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatusController {    // service methods for angular ui

    @RequestMapping("/status")
    @ResponseBody
    public String ping() {
        return "WHP MTraining Status Page";
    }
}

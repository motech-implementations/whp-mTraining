package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.service.impl.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ErrorLogController {

    @Autowired
    private ErrorLogService errorLogService;

    @RequestMapping(value = "/errorLog", method = RequestMethod.GET)
    @ResponseBody
    public String getErrorLogs() {
        return errorLogService.getErrorLogs();
    }
}

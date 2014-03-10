package org.motechproject.ivr.stub;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Controller
public class IVRController {


    @Autowired
    private PublishedCoursesService publishedCoursesService;

    @RequestMapping("/status")
    @ResponseBody
    public String welcome() {
        return "OK";
    }

    @RequestMapping(value = "/publish/courses", method = RequestMethod.POST)
    @ResponseBody
    public String publish(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        String response = IOUtils.toString(reader);
        publishedCoursesService.store(response);
        return "{\"success\":true}";
    }

    @RequestMapping(value = "/courses/latest")
    @ResponseBody
    public List<String> all() throws IOException {
        return publishedCoursesService.all();
    }
}

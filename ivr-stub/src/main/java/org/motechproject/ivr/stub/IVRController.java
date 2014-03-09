package org.motechproject.ivr.stub;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;

@Controller
public class IVRController {

    private static final Stack<String> responses = new Stack<String>();

    @RequestMapping("/status")
    @ResponseBody
    public String welcome() {
        return "OK";
    }

    @RequestMapping(value = "publish/courses", method = RequestMethod.POST)
    @ResponseBody
    public String publish(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringWriter response = new StringWriter();
        IOUtils.copy(reader, response);
        responses.push(response.toString());
        return "{\"success\":true}";
    }

    @RequestMapping(value = "courses/latest")
    @ResponseBody
    public String latestCourse() throws IOException {
        String response = responses.peek();
        ObjectMapper mapper = new ObjectMapper().configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        JsonNode jsonNode = mapper.readTree(response);
        return mapper.writeValueAsString(jsonNode);
    }

}

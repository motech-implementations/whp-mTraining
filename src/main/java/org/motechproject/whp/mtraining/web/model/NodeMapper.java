package org.motechproject.whp.mtraining.web.model;

import java.util.HashMap;
import java.util.Map;

public class NodeMapper {
    public static  Map<String,String> parentNameToChildCLassMap;
    public static  Map<String,String> childToParentNameMap;
        static
        {
            parentNameToChildCLassMap = new HashMap<>();
            childToParentNameMap = new HashMap<>();
            parentNameToChildCLassMap.put("course", null);
            parentNameToChildCLassMap.put("module", "org.motechproject.whp.mtraining.web.model.Course");
            parentNameToChildCLassMap.put("chapter", "org.motechproject.whp.mtraining.web.model.Module");
            parentNameToChildCLassMap.put("message", "org.motechproject.whp.mtraining.web.model.Chapter");
            childToParentNameMap.put("course", "module");
            childToParentNameMap.put("module", "chapter");
            childToParentNameMap.put("chapter", "message");
            childToParentNameMap.put("message", null);
        }
}

package com.alonso.eatelligence.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Component
public class ThymeleafViewRenderer {
    
    @Autowired
    private TemplateEngine templateEngine;

    public String renderFragment(String templateName, String fragmentName, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName + " :: " + fragmentName, context);
    }
}

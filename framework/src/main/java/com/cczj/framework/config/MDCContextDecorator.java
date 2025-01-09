package com.cczj.framework.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;


public class MDCContextDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (previous != null) {
                    MDC.setContextMap(previous);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}

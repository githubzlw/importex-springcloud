package com.importexpress.search.listener;

import com.importexpress.search.common.InitApplicationParameter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitDataListener implements ServletContextListener {
    @Autowired
    private InitApplicationParameter init;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        init.init(servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

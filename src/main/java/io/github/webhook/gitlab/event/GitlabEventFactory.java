package io.github.webhook.gitlab.event;

import io.github.webhook.core.EventHandler;
import io.github.webhook.meta.Webhook;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author EalenXie created on 2023/4/14 12:57
 */
public class GitlabEventFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Map<String, List<EventHandler<Object, Object>>> handlers = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取Gitlab事件处理器
     *
     * @param gitlabEvent Gitlab webhook 事件
     * @return 事件处理器
     */
    @SuppressWarnings("unchecked")
    public List<EventHandler<Object, Object>> getEventHandlers(String gitlabEvent, Webhook webhook) {
        List<EventHandler<Object, Object>> eventHandlers = handlers.get(gitlabEvent);
        if (eventHandlers == null) {
            eventHandlers = new ArrayList<>();
            if (webhook.getNotify() != null) {
                String beanName = String.format("%sNotifyEventHandler", gitlabEvent.replace(" ", ""));
                try {
                    eventHandlers.add((EventHandler<Object, Object>) applicationContext.getBean(capitalize(beanName)));
                    handlers.put(gitlabEvent, eventHandlers);
                } catch (BeansException e) {
                    throw new UnsupportedOperationException(String.format("Can not get EventHandler[%s]", beanName));
                }
            }
        }
        return eventHandlers;
    }

    /**
     * 将字符串的首字母转换为小写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String capitalize(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return str;
        }
        char[] charArray = str.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }
}

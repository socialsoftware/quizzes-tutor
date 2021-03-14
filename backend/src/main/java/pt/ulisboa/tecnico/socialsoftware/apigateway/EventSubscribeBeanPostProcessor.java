package pt.ulisboa.tecnico.socialsoftware.apigateway;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Component
public class EventSubscribeBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    //For each bean that has been initialized by the container, if a method of this bean is annotated with @Subscribe, the bean is registered to the event bus
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        // for each method in the bean
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            // check the annotations on that method
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                // if it contains the Subscribe annotation
                if (annotation.annotationType().equals(Subscribe.class)) {
                    // If this is a Guava @Subscribe annotated event listener method, indicate the bean instance
                    // Correspond to a Guava event listener class, register the bean instance to the Guava event bus
                    eventBus.register(bean);
                    return bean;
                }
            }
        }

        return bean;
    }

    // The event bus bean is created by the Spring IoC container, here you only need to inject the bean through the @Autowired annotation to use the event bus
    @Autowired
    EventBus eventBus;
}
package io.github.natsusai.gateway.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文助手
 *
 * @author liufuhong
 * @since 2019-06-26 17:17
 */

@Component
public class SpringHelper implements ApplicationContextAware {
  private static ApplicationContext applicationContext;

  /**
   * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
   */
  public void setApplicationContext(ApplicationContext applicationContext) {
    SpringHelper.applicationContext = applicationContext;
  }

  /**
   * 取得存储在静态变量中的ApplicationContext.
   */
  public static ApplicationContext getApplicationContext() {
    checkApplicationContext();
    return applicationContext;
  }

  /**
   * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    checkApplicationContext();
    return (T) applicationContext.getBean(name);
  }

  /**
   * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(Class<T> clazz) {
    checkApplicationContext();
    return (T) applicationContext.getBeansOfType(clazz);
  }

  /**
   * 清除applicationContext静态变量.
   */
  public static void cleanApplicationContext() {
    applicationContext = null;
  }

  private static void checkApplicationContext() {
    if (applicationContext == null) {
      throw new IllegalStateException("applicationContext未注入");
    }
  }

  /**
   * 获取当前环境
   */
  public static String getActiveProfile() {
    return applicationContext.getEnvironment().getActiveProfiles()[0];
  }

  /**
   * 获取程序入口类
   */
  public static Class<?> deduceMainApplicationClass() {
    try {
      StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
      for (StackTraceElement stackTraceElement : stackTrace) {
        if ("main".equals(stackTraceElement.getMethodName())) {
          return Class.forName(stackTraceElement.getClassName());
        }
      }
    }
    catch (ClassNotFoundException ex) {
      // Swallow and continue
    }
    return null;
  }
}
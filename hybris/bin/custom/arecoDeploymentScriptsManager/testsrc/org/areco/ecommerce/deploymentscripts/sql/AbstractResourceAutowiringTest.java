package org.areco.ecommerce.deploymentscripts.sql;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This test doesn't have test listeners or runners.
 * Created by arobirosa on 24.01.15.
 */
@Ignore
public abstract class AbstractResourceAutowiringTest {

        private static final Logger LOG = Logger.getLogger(AbstractResourceAutowiringTest.class);

        @Before
        public void prepareApplicationContextAndSession() throws Exception {
                final ApplicationContext applicationContext = Registry.getApplicationContext();

                autowireProperties(applicationContext);

                // Because of transactional stuff the number series will be deleted after each test.
                // The init method of a bean will only be called once, so init all number series at each test.
                for (final PersistentKeyGenerator generator : applicationContext.getBeansOfType(PersistentKeyGenerator.class).values()) {
                        generator.init();
                }
        }

        protected void autowireProperties(final ApplicationContext applicationContext) {
                final Object test = this;
                final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
                final Set<String> missing = new LinkedHashSet<String>();

                ReflectionUtils.doWithFields(test.getClass(), new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                                final Resource r = field.getAnnotation(Resource.class);
                                if (r != null) {
                                        field.setAccessible(true);
                                        Object bean = ReflectionUtils.getField(field, test);
                                        if (bean == null) {
                                                final String beanName = getBeanName(r, field);
                                                try {
                                                        bean = beanFactory.getBean(beanName, field.getType());
                                                        if (bean != null) {
                                                                ReflectionUtils.setField(field, test, bean);
                                                        }
                                                } catch (final BeansException e) {
                                                        LOG.error("error fetching bean " + beanName + " : " + e.getMessage(), e);
                                                }
                                                if (bean == null) {
                                                        missing.add(field.getName());
                                                }
                                        }
                                }
                        }
                });
                if (!missing.isEmpty()) {
                        throw new IllegalStateException("test " + getClass().getSimpleName()
                                + " is not properly initialized - missing bean references " + missing);
                }
        }

        protected String getBeanName(final Resource r, final Field field) {
                if (r.mappedName() != null && r.mappedName().length() > 0) {
                        return r.mappedName();
                } else if (r.name() != null && r.name().length() > 0) {
                        return r.name();
                } else {
                        return field.getName();
                }
        }

        protected ApplicationContext getApplicationContext() {
                return Registry.getApplicationContext();
        }
}


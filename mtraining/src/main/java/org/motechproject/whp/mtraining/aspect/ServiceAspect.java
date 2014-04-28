package org.motechproject.whp.mtraining.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.motechproject.whp.mtraining.PersistenceClassLoader;
import org.motechproject.whp.mtraining.repository.RepositorySupport;
import org.springframework.stereotype.Component;

/**
 * The main purpose of the <code>ServiceAspect</code> class is change class loader for
 * current thread while methods inside service classes are executed. After performing
 * a service method, the old class loader is restored to current thread.
 * Class loader change is required so that dataNucleus framework has visibility into all the required jars
 *
 * @see org.aspectj.lang.annotation.Aspect
 * @see org.motechproject.whp.mtraining.repository.RepositorySupport
 */
@Aspect
@Component
public class ServiceAspect {

    @Around("within(org.motechproject.whp.mtraining.repository..*)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        checkTarget(joinPoint.getTarget());
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(PersistenceClassLoader.INSTANCE);

            return joinPoint.proceed();
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }

    private void checkTarget(Object target) {
        if (!(target instanceof RepositorySupport)) {
            throw new IllegalStateException(
                    "The target class should extend " + PersistenceClassLoader.class.getName()
            );
        }
    }

}

package org.benaya.learn.polyglotwithvenv.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static org.python.Python_h_1.PyEval_SaveThread;
import static org.python.Python_h_1.PyGILState_Ensure;

@Aspect
@Component
@RequiredArgsConstructor
public class PythonGilAspect {
    @Around("@annotation(org.benaya.learn.polyglotwithvenv.annotation.PythonGilLock)")
    public Object managePythonGIL(ProceedingJoinPoint joinPoint) throws Throwable {
        // Call PyGILState_Ensure()
        PyGILState_Ensure();
        try {
            // Proceed with the method execution
            return joinPoint.proceed();
        } finally {
            // Call PyEval_SaveThread()
            PyEval_SaveThread();
        }
    }
}

package org.benaya.learn.polyglotwithvenv.memory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.python.PyThreadState;
import org.springframework.stereotype.Component;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.foreign.MemorySegment.NULL;
import static org.benaya.learn.polyglotwithvenv.constants.PythonScripts.SCRIPT;
import static org.python.Python_h_1.*;

@Component
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class ArenaSystem {
    private final ScheduledExecutorService executorService;
    private Arena arena;
    private MemorySegment pythonGetEmbeddingsFunction;

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        log.info("Initializing Arena System");
        Py_Initialize();
        PyEval_InitThreads();
        PyThreadState mainThreadState = null;
        var mainThreadStateMemory = PyThreadState_Get();
        arena = executorService.submit(Arena::ofShared).get();
        MemorySegment scriptStringSegment = arena.allocateFrom(SCRIPT);
        PyRun_SimpleStringFlags(scriptStringSegment, NULL);
        MemorySegment moduleName = arena.allocateFrom("__main__");
        MemorySegment module = PyImport_ImportModule(moduleName);
        MemorySegment funcName = arena.allocateFrom("encode_sentences");
        setPythonGetEmbeddingsFunction(PyObject_GetAttrString(module, funcName));
    }


    @PreDestroy
    public void destroy() {
        log.info("Destroying Arena System");
        arena.close();
        Py_Finalize();
    }

    public Future<MemorySegment> allocateStringAndReturnMemorySegment(String sentence) {
        return getExecutorService()
                .submit(() -> arena.allocateFrom(sentence));
    }


}

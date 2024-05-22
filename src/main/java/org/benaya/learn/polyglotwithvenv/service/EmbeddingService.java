package org.benaya.learn.polyglotwithvenv.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.benaya.learn.polyglotwithvenv.annotation.PythonGilLock;
import org.springframework.stereotype.Service;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

import static java.lang.foreign.MemorySegment.NULL;
import static org.benaya.learn.polyglotwithvenv.constants.PythonScripts.EMBEDDING_SCRIPT;
import static org.python.Python_h_1.*;
import static org.python.Python_h_2.*;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private Arena arena;
    private MemorySegment pFunc;

    @PostConstruct
    public void init() {
        Py_Initialize();
        PyEval_SaveThread();
        arena = Arena.global();
        PyGILState_Ensure();
        MemorySegment str = arena.allocateFrom(EMBEDDING_SCRIPT);
        PyRun_SimpleStringFlags(str, NULL);
        MemorySegment moduleName = arena.allocateFrom("__main__");
        MemorySegment module = PyImport_ImportModule(moduleName);
        if (module.address() == 0) {
            throw new RuntimeException("Failed to import Python module");
        }
        MemorySegment funcName = arena.allocateFrom("encode_sentences");
        pFunc = PyObject_GetAttrString(module, funcName);
        if (pFunc.address() == 0 || PyCallable_Check(pFunc) == 0) {
            throw new RuntimeException("Failed to load Python function");
        }
        PyEval_SaveThread();
    }

    @PythonGilLock
    public List<List<Double>> getEmbeddingsForSentences(List<String> sentences) {
        MemorySegment pList = convertJavaStringListToPythonList(sentences);
        MemorySegment pArgs = createPythonArgsFromPythonList(pList);
        MemorySegment pValue = callFunctionWithArgs(pArgs);
        return convertResultToJavaListOfLists(pValue);
    }

    @PythonGilLock
    private MemorySegment convertJavaStringListToPythonList(List<String> sentences) {
        MemorySegment pList = PyList_New(sentences.size());
        if (pList.address() == 0) {
            throw new RuntimeException("Failed to create Python list");
        }

        for (int i = 0; i < sentences.size(); i++) {
            MemorySegment pyStr = PyUnicode_FromString(arena.allocateFrom(sentences.get(i)));
            if (pyStr.address() == 0) {
                throw new RuntimeException("Failed to create Python string");
            }
            PyList_SetItem(pList, i, pyStr);
        }
        return pList;
    }

    @PythonGilLock
    private MemorySegment createPythonArgsFromPythonList(MemorySegment pList) {
        MemorySegment pArgs = PyTuple_New(1);
        if (pArgs.address() == 0) {
            throw new RuntimeException("Failed to create Python tuple");
        }
        PyTuple_SetItem(pArgs, 0, pList);
        return pArgs;
    }

    @PythonGilLock
    private MemorySegment callFunctionWithArgs(MemorySegment pArgs) {
        MemorySegment pValue = PyObject_CallObject(pFunc, pArgs);
        if (pValue.address() == 0) {
            throw new RuntimeException("Failed to call Python function");
        }
        return pValue;
    }

    @PythonGilLock
    private List<List<Double>> convertResultToJavaListOfLists(MemorySegment pValue) {
        List<List<Double>> embeddings = new ArrayList<>();
        long size = PyList_Size(pValue);
        for (int i = 0; i < size; i++) {
            MemorySegment item = PyList_GetItem(pValue, i);
            long innerSize = PyList_Size(item);
            List<Double> embeddingsCurrent = new ArrayList<>();
            for (int j = 0; j < innerSize; j++) {
                MemorySegment innerItem = PyList_GetItem(item, j);
                embeddingsCurrent.add(PyFloat_AsDouble(innerItem));
            }
            embeddings.add(embeddingsCurrent);
        }
        return embeddings;
    }

    @PreDestroy
    public void finalizePythonGil() {
        PyGILState_Ensure();
        Py_Finalize();
    }
}

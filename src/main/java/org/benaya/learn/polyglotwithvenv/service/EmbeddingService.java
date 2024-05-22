package org.benaya.learn.polyglotwithvenv.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

import static java.lang.foreign.MemorySegment.NULL;
import static org.python.Python_h_1.*;
import static org.python.Python_h_2.PyObject_GetAttrString;
import static org.python.Python_h_2.PyObject_IsTrue;
import static org.python.Python_h_2.PyUnicode_FromString;
import static org.benaya.learn.polyglotwithvenv.constants.PythonScripts.*;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private Arena arena;
    private MemorySegment pFunc;
    private MemorySegment module;

    @PostConstruct
    public void init() {
        Py_Initialize();
        PyEval_SaveThread();
        arena = Arena.global();
        PyGILState_Ensure();
        MemorySegment str = arena.allocateFrom(EMBEDDING_SCRIPT);
        PyRun_SimpleStringFlags(str, NULL);
        MemorySegment moduleName = arena.allocateFrom("__main__");
        module = PyImport_ImportModule(moduleName);
        MemorySegment funcName = arena.allocateFrom("encode_sentences");
        pFunc = PyObject_GetAttrString(module, funcName);
        PyEval_SaveThread();
    }

    public List<List<Double>> getEmbeddingsForSentences(List<String> sentences) {
        List<List<Double>> embeddings = new ArrayList<>();
        PyGILState_Ensure();
        // Create a Python list from Java array of strings
        String[] inputSentences = sentences.toArray(new String[0]);
        MemorySegment pList = PyList_New(inputSentences.length);
        for (int i = 0; i < inputSentences.length; i++) {
            PyList_SetItem(pList, i, PyUnicode_FromString(arena.allocateFrom(inputSentences[i])));
        }
        MemorySegment pArgs = PyTuple_New(1);
        PyTuple_SetItem(pArgs, 0, pList);
        MemorySegment pValue = PyObject_CallObject(pFunc, pArgs);

        // Call the is_list function to check if the result is a list
        MemorySegment isListFuncName = arena.allocateFrom("is_list");
        MemorySegment isListFunc = PyObject_GetAttrString(module, isListFuncName);
        MemorySegment isListArgs = PyTuple_New(1);
        PyTuple_SetItem(isListArgs, 0, pValue);
        MemorySegment isListResult = PyObject_CallObject(isListFunc, isListArgs);
        if (PyObject_IsTrue(isListResult) != 0) {
            long size = PyList_Size(pValue);

            for (int i = 0; i < size; i++) {
                MemorySegment item = PyList_GetItem(pValue, i);
                MemorySegment isInnerListArgs = PyTuple_New(1);
                PyTuple_SetItem(isInnerListArgs, 0, item);
                MemorySegment isInnerListResult = PyObject_CallObject(isListFunc, isInnerListArgs);
                if (PyObject_IsTrue(isInnerListResult) != 0) {
                    long innerSize = PyList_Size(item);
                    List<Double> embeddings_current = new ArrayList<>();
                    for (int j = 0; j < innerSize; j++) {
                        MemorySegment innerItem = PyList_GetItem(item, j);
                        embeddings_current.add(PyFloat_AsDouble(innerItem));
                    }
                    embeddings.add(embeddings_current);
                }
            }
        }
        PyEval_SaveThread();
        return embeddings;
    }

    @PreDestroy
    public void finalizePythonGil() {
        PyGILState_Ensure();
        Py_Finalize();
    }
}
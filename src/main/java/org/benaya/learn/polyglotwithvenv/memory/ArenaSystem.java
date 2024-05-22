//package org.benaya.learn.polyglotwithvenv.memory;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.Getter;
//
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.lang.foreign.*;
//import java.lang.invoke.MethodType;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//import static java.lang.foreign.MemorySegment.NULL;
//import static org.benaya.learn.polyglotwithvenv.constants.PythonScripts.SCRIPT;
//import static org.python.Python_h_1.*;
//
//@Component
//@Slf4j
//@Getter
//@Setter
//@RequiredArgsConstructor
//public class ArenaSystem {
//    private Arena arena;
//    private int gilState = -1;
//
//    @PostConstruct
//    public void init() throws ExecutionException, InterruptedException {
//        log.info("Initializing Arena System");
//        initializePythonGil();
//        arena = Arena.ofShared();
//        setGilState(acquireGilState());
//        MemorySegment scriptStringSegment = arena.allocateFrom(SCRIPT);
//        PyRun_SimpleStringFlags(scriptStringSegment, NULL);
//        releaseGil(getGilState());
//        setGilState(-1);
//    }
//
//
//    @PreDestroy
//    public void destroy() {
//        log.info("Destroying Arena System");
//        arena.close();
//        finalizePythonGil();
//    }
//
//    public MemorySegment allocateStringAndReturnMemorySegment(String sentence) {
//        return arena.allocateFrom(sentence);
//    }
//
//
//    public void initializePythonGil() {
//        Py_Initialize();
//        PyEval_SaveThread();
//    }
//
//    public void finalizePythonGil() {
//        PyGILState_Ensure();
//        Py_Finalize();
//    }
//
//    public int acquireGilState() {
//        System.out.println("Acquiring GIL");
//        return PyGILState_Ensure();
//    }
//
//    public void releaseGil(int state) {
//        System.out.println("Releasing GIL");
//        PyGILState_Release(state);
//    }
//
//    public List<List<Double>> getEmbeddingsForSentences(List<String> sentences) {
//        setGilState(acquireGilState());
//        MemorySegment sentencesList = createPythonListFromJavaSentencesList(sentences);
//        MemorySegment pArgs = createPythonArgsFromPythonList(sentencesList);
//        MemorySegment pEmbeddings = runFunctionAndGetResult(pArgs);
//        List<List<Double>> embeddings = getEmbeddingListFromResult(pEmbeddings);
//        releaseGil(getGilState());
//        setGilState(-1);
//        return embeddings;
//    }
//
//    private MemorySegment runFunctionAndGetResult(MemorySegment pArgs) {
//        MemorySegment moduleName = arena.allocateFrom("__main__");
//        var module = PyImport_ImportModule(moduleName);
//        var funcName = arena.allocateFrom("encode_sentences");
//        var pFunc = PyObject_GetAttrString(module, funcName);
//        return PyObject_CallObject(pFunc, pArgs);
//    }
//
//    private MemorySegment createPythonArgsFromPythonList(MemorySegment pythonList) {
//        MemorySegment args = PyTuple_New(1);
//        PyTuple_SetItem(args, 0, pythonList);
//        return args;
//    }
//
//    private MemorySegment createPythonListFromJavaSentencesList(List<String> sentences) {
//        MemorySegment pythonList = PyList_New(sentences.size());
//        for (int i = 0; i < sentences.size(); i++) {
//            MemorySegment sentence = allocateStringAndReturnMemorySegment(sentences.get(i));
//            PyList_SetItem(pythonList, i, sentence);
//        }
//        return pythonList;
//    }
//
//    private List<List<Double>> getEmbeddingListFromResult(MemorySegment pResult) {
//        checkReturnValue(pResult);
//        long size = PyList_Size(pResult);
//        List<List<Double>> embeddings = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            MemorySegment doubleList = PyList_GetItem(pResult, i);
//            embeddings.add(getJavaListFromPythonList(doubleList));
//        }
//        return embeddings;
//    }
//
//    private List<Double> getJavaListFromPythonList(MemorySegment pythonList) {
//        checkReturnValue(pythonList);
//        long size = PyList_Size(pythonList);
//        List<Double> embeddingsVector = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            MemorySegment item = PyList_GetItem(pythonList, i);
//            embeddingsVector.add(PyFloat_AsDouble(item));
//        }
//        return embeddingsVector;
//    }
//
//
//    private void checkReturnValue(MemorySegment pResult) {
//        if (pResult == null || pResult.address() == 0) {
//            throw new RuntimeException("Python function returned null");
//        }
//    }
//
//}

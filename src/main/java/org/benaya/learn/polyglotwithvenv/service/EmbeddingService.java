package org.benaya.learn.polyglotwithvenv.service;

import lombok.RequiredArgsConstructor;
import org.benaya.learn.polyglotwithvenv.memory.ArenaSystem;
import org.springframework.stereotype.Service;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.python.Python_h_1.*;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final ArenaSystem arenaSystem;
    public List<List<Double>> getEmbeddingsForSentences(List<String> sentences) throws ExecutionException, InterruptedException {
        MemorySegment sentencesList = createPythonListFromJavaSentencesList(sentences);
        MemorySegment pArgs = createPythonArgsFromPythonList(sentencesList);
        MemorySegment pythonGetEmbeddingsFunction = arenaSystem.getPythonGetEmbeddingsFunction();
        MemorySegment pEmbeddings = PyObject_CallObject(pythonGetEmbeddingsFunction, pArgs);
        return getEmbeddingListFromResult(pEmbeddings);
    }

    MemorySegment createPythonArgsFromPythonList(MemorySegment pythonList) {
        MemorySegment args = PyTuple_New(1);
        PyTuple_SetItem(args, 0, pythonList);
        return args;
    }

    MemorySegment createPythonListFromJavaSentencesList(List<String> sentences) throws ExecutionException, InterruptedException {
        MemorySegment pythonList = PyList_New(sentences.size());
        for (int i = 0; i < sentences.size(); i++) {
            Future<MemorySegment> sentenceFuture = arenaSystem.allocateStringAndReturnMemorySegment(sentences.get(i));
            MemorySegment sentence = sentenceFuture.get();
            PyList_SetItem(pythonList, i, sentence);
        }
        return pythonList;
    }

    List<List<Double>> getEmbeddingListFromResult(MemorySegment pResult) {
        checkReturnValue(pResult);
        long size = PyList_Size(pResult);
        List<List<Double>> embeddings = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MemorySegment doubleList = PyList_GetItem(pResult, i);
            embeddings.add(getJavaListFromPythonList(doubleList));
        }
        return embeddings;
    }

    List<Double> getJavaListFromPythonList(MemorySegment pythonList) {
        checkReturnValue(pythonList);
        long size = PyList_Size(pythonList);
        List<Double> embeddingsVector = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MemorySegment item = PyList_GetItem(pythonList, i);
            embeddingsVector.add(PyFloat_AsDouble(item));
        }
        return embeddingsVector;
    }


    void checkReturnValue(MemorySegment pResult) {
        if (pResult == null || pResult.address() == 0) {
            throw new RuntimeException("Python function returned null");
        }
    }


}
package org.benaya.learn.polyglotwithvenv.constants;

public class PythonScripts {
    public static final String SCRIPT = """
            from sentence_transformers import SentenceTransformer
            import numpy as np
            model = SentenceTransformer('nomic-ai/nomic-embed-text-v1.5', trust_remote_code=True)
            def encode_sentences(sentences):
                embeddings = model.encode(sentences)
                return embeddings.tolist()
            def is_list(obj):
                return isinstance(obj, list)
            """;
}

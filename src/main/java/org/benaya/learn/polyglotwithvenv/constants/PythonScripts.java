package org.benaya.learn.polyglotwithvenv.constants;

public class PythonScripts {
    public static final String EMBEDDING_SCRIPT = """
            from sentence_transformers import SentenceTransformer
            import numpy as np
            model = SentenceTransformer('nomic-ai/nomic-embed-text-v1.5', trust_remote_code=True)
            def encode_sentences(sentences):
                embeddings = model.encode(sentences)
                return embeddings.tolist()
            def is_list(obj):
                return isinstance(obj, list)
            """;
    public static final String EMBEDDING_SCRIPT_JSON = """
            from sentence_transformers import SentenceTransformer
            import numpy as np
            import json
            model = SentenceTransformer('nomic-ai/nomic-embed-text-v1.5', trust_remote_code=True)
            def encode_sentences(sentences):
                embeddings = model.encode(sentences)
                return json.dumps(embeddings.tolist())
            def is_list(obj):
                return isinstance(obj, list)
            """;
}

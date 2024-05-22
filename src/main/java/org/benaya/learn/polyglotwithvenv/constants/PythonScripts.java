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
//    public static final String CHAT_SCRIPT = """
//            from transformers import AutoModelForCausalLM, AutoTokenizer
//            model = AutoModelForCausalLM.from_pretrained("dicta-il/dictalm2.0-instruct-GPTQ", device_map=device)
//            tokenizer = AutoTokenizer.from_pretrained("dicta-il/dictalm2.0-instruct-GPTQ")
//            def generate_response(user_input):
//                input_ids = tokenizer.encode(user_input, return_tensors='pt')
//                generated_ids = model.generate(encoded, max_new_tokens=50, do_sample=True)
//                return tokenizer.batch_decode(generated_ids)
//            """;
}

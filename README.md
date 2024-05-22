## java polyglot with python and jextract
- using jextract to generate java bindings for C libraries, using Python.h header file.
- seamlessly using 3rd party python libs, like sentence transformers.
- using low level python c api for converting java to python types, with memory segments.
- using ffi and memory api to encapsulate off-heap memory usage in an Arena. 
- carefully managing mutex/locks for running python code in a multi-threaded environment.

pre-requisites:
- python3
- java22
- maven

run: 
- run the compile_python.sh script to compile the python code and generate the java bindings.
- run the application, using the jvm argument --enable-native-access=ALL-UNNAMED

send embedding request:
curl -X POST "http://localhost:8080/embeddings/multiple_sentences" -H "Content-Type: application/json" -d '["The cat jumped over fences quickly.", "Bright stars illuminated the night sky."]'

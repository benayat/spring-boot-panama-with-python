## java polyglot with python and jextract
- using jextract to generate java bindings for C libraries, using Python.h header file.
- seamlessly using 3rd party python libs, like sentence transformers.
- using low level python c api for converting java to python types, with memory segments.
- using ffi and memory api to encapsulate off-heap memory usage in an Arena. 
- carefully managing mutex/locks for running python code in a multi-threaded environment.

### Purpose and Scope
- The sole purpose of this repo is to show the possibility of running python, including 3rd part libs, in a java application, and directly.
- For that purpose,I ignored performance and memory usage, and focused on the code readability and reusability - using spring aop and custom annotations.
- If you're interested in a more production ready and less JVM crashing solution, look up JEP.
- For future reference, If you want to progress in my(Jextract) direction, check out python cffi, and make a specific c header instead of using the whole Python header. it could make things much simpler. 

### How does it work?
- Python interpreter is actually written in C, and in every python installation, you can find the Python.h header file, which is the C API for python.
- Using the Jextract tool, I generated the java bindings for the Python.h header file, and used it to run python code in a java application.
- Python GIL can only be run in a single thread, and since spring boot runs on a multi-threaded environment, acquiring and releasing the lock is needed. that's managed via PyGILState_Ensure and PyEval_SaveThread c ABI methods.
- To make it neater, I used spring aop and custom annotations to encapsulate this logic in the @PythonGilLock annotation, and make it readable and reusable.

#### pre-requisites:
- python3
- Jextract installed
- java22

run: 
- open terminal(or git-bash on windows) and navigate to the project directory.
- run the compile_python.sh script to compile the python code and generate the java bindings.
- run `./mvnw clean package -DskipTests` to create a jar in 'target' directory.
- run java --enable-native-access=ALL-UNNAMED -jar <path to jar>

send embedding request:
curl -X POST "http://localhost:8080/embeddings/multiple_sentences" -H "Content-Type: application/json" -d '["The cat jumped over fences quickly.", "Bright stars illuminated the night sky."]'

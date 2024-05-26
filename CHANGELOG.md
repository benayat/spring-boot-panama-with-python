### Changelog

#### 0.0.1-SNAPSHOT
- Initial code.
- added python bindings using jextract. 
- changed arena type to global.
- init python gil with @PostConstruct and @PreDestroy methods.
- added locks with PyGILState_Ensure and PyEval_SaveThread c ABI methods.

#### 1.0.0-SNAPSHOT
- cleaned the code with spring boot aop and @PythonGilLock custom annotation.

#### 1.0.0
- using Json marshaling with objectMapper, instead of allocating multiple memory segments directly.
- updated readme with more detailed explanation of the purpose and running instructions of this repo.

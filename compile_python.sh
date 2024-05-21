#!/usr/bin/env bash
source polyglot-env/Scripts/activate
# Check if the virtual environment is activated
if [[ "$VIRTUAL_ENV" != "" ]]; then
    echo "Virtual environment activated: $VIRTUAL_ENV"
else
    echo "Failed to activate virtual environment"
    exit 1
fi
INCLUDE_DIR=$(python -c "import sysconfig; print(sysconfig.get_paths()['include'])")
echo "Include directory: $INCLUDE_DIR"

PYTHON_LIB=python311
TARGET_PACKAGE=org.python
jextract --output src/main/java -I "$INCLUDE_DIR" -l "$PYTHON_LIB" -t "$TARGET_PACKAGE" "$INCLUDE_DIR/Python.h"
# javac --source=22 -d . src/org/python/*.java

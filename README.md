# ksp-godegen-examples
Repository with some examples of Kotlin code generation and KSP usage

- **DebugLog**: Adds a simple annotation `@DebugLog` for the Kotlin methods.
    Annotation processing gathers all annotated methods into a list and outputs
    basic information about them into a `debuglog-report.txt` file.
  - `debug-log-processor`: submodule with the processor.
  - `debug-log-app`: submodule with the example usage.

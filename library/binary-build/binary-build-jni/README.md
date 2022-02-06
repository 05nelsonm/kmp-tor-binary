# binary-build-jni

JNI Bindings for running tor natively on the JVM

Highly experimental. Issues currently exist surrounding the ability to clear Tor's
static state variables upon non-clean exit, which result in a SIG 11 upon next start.

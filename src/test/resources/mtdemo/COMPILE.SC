;  ------------------------------------------------------------------

;  This script builds the library.
;  You must play this script at least once before running the
;  application for the first time.

Clear
@ 3,0 ?? "Playing all procedure scripts to build the library"
out_lib = "MTDemo"      ;library name to output procedures to
CreateLib out_lib       ;create the library

Proc Closed Compile()   ;procedure to play each library-building script
   UseVars out_lib
   ? "   Main"         Play "Main"
   ? "   Common"       Play "Common"
   ? "   Form2"        Play "Form2"
   ? "   Form3"        Play "Form3"
   ? "   Form3i"       Play "Form3i"
   ? "   Form4"        Play "Form4"
   ? "   Form5"        Play "Form5"
EndProc

WriteLib out_lib Compile   ;closed proc must be in a library
Release Procs Compile
autolib = out_lib
Compile()               ;this is a closed procedure to conserve memory

If not isassigned(avoiding_compile) or not avoiding_compile then
   Release Vars out_lib
Endif

;  ------------------------------------------------------------------


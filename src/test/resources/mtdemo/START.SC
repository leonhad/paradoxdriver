;  ------------------------------------------------------------------

;  This is the main script which you play to start the application.

;  Initialization

Clear
Reset

;  Test Paradox Version

If version()<3.0 then
   Beep
   Quit "You must upgrade to Version 3.0 of Paradox before proceeding..."
Endif
If not isblankzero() then
   Beep
   Quit "You must configure Paradox so that Blanks=Zero"
            +" before proeceeding..."
Endif

;  Test for the presence of the main library.
;  If it's missing, then create it

autolib = "MTDemo"
If not isfile(autolib+".Lib") then
   Beep
   Quit "You must play the COMPILE script to build the library first..."
Endif

;  Assign the SetKey [Ctrl-F10], to re-start the application

SetKey "F30" Clear Reset autolib="MTDemo" Main()

;  Start the application

Main()

;  ------------------------------------------------------------------


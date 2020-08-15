;  ------------------------------------------------------------------

;  This script contains the main procedures to run the application:

;     MAIN()
;     MAIN_MENU()

;  ------------------------------------------------------------------

If not isassigned(out_lib) then
   If isassigned(avoiding_compile) and avoiding_compile then
      out_lib = "MTDemo"
   Else
      Beep
      Quit "You can't play this script!  Play COMPILE first, then START..."
   Endif
Endif

;  ------------------------------------------------------------------

Proc Closed Main()

;  Main procedure:  Initialize environment and call main menu routine

UseVars
   autolib        ;auto-load library

;  Initialization

SetSwap 30000

;  Bring up the main menu

Main_Menu()

;  All done

EndProc

WriteLib out_lib Main
Release Procs Main

;  ------------------------------------------------------------------

Proc Main_Menu()

;  Main menu procedure

Private
   main_menu_sel        ;main menu selection

;  Bring up the menu over and over again until the user quits

While True
   Clear
   @ 2,0 ?? fill("Í",80)
   Reset
   Message "Which data entry demo would you like to perform?"

   ShowMenu
      "2":     "Basic Vidorder/Rental Entry (form Vidorder.F2)",
      "3":     "Rental Table is Keyed by Line # (form Vidorder.F3)",
      "3-I":   "Keyed by Line #, with [Ins] Capability (form Vidorder.F3)",
      "4":     "Unlinked Forms for Films & Tax Rates (form Vidorder.F4)",
      "5/6":   "Two Forms Using Shadow Table Technique (form Vidorder.F5&6)",
      "Quit":  "All Done.  Return to Paradox"
   to main_menu_sel

   Message ""
   @ 1,0 Clear Eol
   @ 0,0 Clear Eol

;  Perform the selected procedure

   Switch
      Case main_menu_sel="2":    Form2()
      Case main_menu_sel="3":    Form3()
      Case main_menu_sel="3-I":  Form3i()
      Case main_menu_sel="4":    Form4()
      Case main_menu_sel="5/6":  Form5()
      Otherwise:                 QuitLoop
   EndSwitch
EndWhile

;  All done

EndProc

WriteLib out_lib Main_Menu
Release Procs Main_Menu

;  ------------------------------------------------------------------


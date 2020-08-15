;  ------------------------------------------------------------------

;  This script contains the procedures that are common to more than
;  one form:

;     SELECT_VIDORDER()
;     POST_NEW_VIDORDER()
;     TEST_REQD_FIELDS()
;     IS_NEW_RENTAL()
;     POST_NEW_RENTAL()
;     IS_NEW_KRENTAL()
;     CALC_TAX_RATE()

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

Proc Select_Vidorder(form_no)

;  Shows the user all the orders, and lets them select one to edit.

;  "form_no" is the form # to be displayed

Private
   wait_key,      ;the key the user pressed in response to the Wait
   wait_msg       ;the message to show the user on the Wait

;  Initialization

wait_msg = "Which order do you want to view and edit?"

;  Let the user select an order

While True

   If sysmode()="CoEdit" then Do_it! Endif      ;get back to main mode
   If isformview() then FormKey Endif           ;get back to table view
   CtrlHome                                     ;start at the left margin

;  Show the table to the user

   Wait Table
      Prompt "Select an order to view and edit",
             "Press [F2] to select it, [Ins] to add a new order," +
             " or [Esc] to quit"
      Message wait_msg
      Until "F2","Ins","Esc","Dos","DosBig"

;  Test which function key the user pressed

   wait_key = retval    ;remember which key the user pressed
   wait_msg = ""        ;no error message yet
   @ 1,0 Clear Eol
   @ 0,0 Clear Eol ?? "One Moment Please" 
   Style Blink ?? "..." Style

   is_exiting = False               ;assume it's not [Esc]
   is_inserting = False             ;assume it's not [Ins]

   Switch
      Case wait_key="Esc":    is_exiting = True    ;user pressed [Esc]
                              QuitLoop             ;so we're done
      Case wait_key="Ins":    is_inserting = True  ;user pressed [Ins]
      Case wait_key="F2":                          ;user pressed [F2]
         If isempty(table()) then Beep Loop Endif  ;nothing to select
      Otherwise:              Beep Loop            ;illegal key
   EndSwitch

;  Initialize the selected record for editing

   @ 2,0 Clear Eos ?? fill("Í",80)
   CoEditKey                  ;enter co-edit mode
   is_locked = False          ;the record is not locked yet
   If is_form_picked then     ;form has been picked before
      FormKey                 ;so just pick it
   Else                       ;this is the first time
      PickForm form_no        ;so tell Paradox which form to use
   Endif

   If is_inserting then       ;we're inserting a new record
      Ins                     ;so insert the record
   Else                       ;we're editing an existing record
      LockRecord              ;attempt to lock the record
      If not retval then      ;could not lock it
         Beep
         wait_msg = errormessage()     ;display the message
         UnLockRecord         ;unlock the record, just in case
         Loop                 ;try again
      Endif
      is_locked = True        ;the record is locked now
      ImageRights Update      ;user can change the record, but not the key
   Endif

   QuitLoop                   ;exit to process this record   
EndWhile

;  All done

EndProc

WriteLib out_lib Select_Vidorder
Release Procs Select_Vidorder

;  ------------------------------------------------------------------

Proc Post_New_Vidorder()

;  When the user has entered a new record into the Vidorder table,
;  this procedure validates it and attempts to lock it.

;  Test for [Esc] to cancel

If wait_key="Esc" then                 ;the [Esc] key
   Undo Undo                           ;remove the record
   is_exiting = True                   ;the user wants to exit from the form
   Return                              ;all done
Endif

;  Test for illegal keys

If wait_key<>"F2" and wait_key<>"F4" then    ;an illegal key
   Beep Return
Endif

;  Make sure the key fields are filled in

Test_Reqd_Fields(n_key_fields)
If not retval then
   Beep
   wait_msg = "You must fill in a "+field()
   Return
Endif

;  Attempt to lock the record.
;  Assume that failure to lock it means a key violation.

LockRecord           ;attempt to post the record
If not retval then   ;we were unable to lock it
   Beep
   MoveTo [Date]
   [] = []           ;return to unposted state
   wait_msg = "This date/customer record already exists"
   Return
Endif

;  The record has been successfully locked

is_locked = True     ;the record is now locked
ImageRights Update   ;don't let the user change the key anymore

;  All done

EndProc

WriteLib out_lib Post_New_Vidorder
Release Procs Post_New_Vidorder

;  ------------------------------------------------------------------

Proc Test_Reqd_Fields(n)

;  Procedure to test required fields in a record.
;  "N" is the number of fields to test.
;  The field names must be stored in an array "REQD_FIELDS."
;  Returns True if they are all filled in, False if they are not.
;  If a required field is blank, the procedure moves the cursor there.

Private
   orig_field,    ;the cursor field at the beginning of the procedure
   i              ;temporary counter

;  Test each field

orig_field = field()                   ;remember the current field
For i from 1 to n                      ;do each field in the array
   MoveTo Field reqd_fields[i]         ;move to the field
   If isblank([]) then                 ;it's blank
      Return False                     ;so we've failed
   Endif
EndFor
MoveTo Field orig_field                ;return to the original field

Return True                            ;successful completion

EndProc

WriteLib out_lib Test_Reqd_Fields
Release Procs Test_Reqd_Fields

;  ------------------------------------------------------------------

Proc Is_New_Rental()

;  This procedure tests whether we've arrived at a new, unposted
;  rental detail record

;  Unfortunately, I know of know general-purpose foolproof way
;  of telling whether a detail table is empty from CoEdit mode,
;  when we first arrive to it.  The PAL function NIMAGERECORDS()
;  returns the value 1 if the table is empty -- but a 1 might
;  mean the table is empty, or that it already had 1 record in it.
;  This procedure tests several fields in the record, and it
;  assumes that if the field is blank, then the record is a newly
;  inserted one.

If nimagerecords()>1 then           ;there are records here
   Return False                     ;so we must be on one of them
Endif
If isblank([Film #]) and isblank([Rental Fee]) then      ;these fields blank
   Return True                      ;we assume it's a new record
Endif
Return False                        ;otherwise, it's an old record

EndProc

WriteLib out_lib Is_New_Rental
Release Procs Is_New_Rental

;  ------------------------------------------------------------------

Proc Post_New_Rental()

;  When the user has entered a new record into the Rental table,
;  this procedure validates it and attempts to lock it.

;  Make sure the key field is filled in

If isblank([Film #]) then           ;it's blank
   is_del_key = True                ;delete the record
   Return
Endif

;  Attempt to lock the record.
;  Assume that failure to lock it means a key violation.

LockRecord           ;attempt to post the record
If not retval then   ;we were unable to lock it
   Beep
   MoveTo [Film #]
   [] = []           ;return to unposted state
   wait_msg = "This film record already exists in this order"
   Return
Endif

;  The record has been successfully locked

is_locked_detail = True       ;the record is now locked
ImageRights Update            ;don't let the user change the key anymore

;  All done

EndProc

WriteLib out_lib Post_New_Rental
Release Procs Post_New_Rental

;  ------------------------------------------------------------------


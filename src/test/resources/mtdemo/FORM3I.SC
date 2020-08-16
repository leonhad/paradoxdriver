;  ------------------------------------------------------------------

;  This script contains the procedures to run form #3.
;  This form demonstrates calculation of line #'s in new detail
;  records automatically by the program.
;  The program calculates the line # for a new record inserted
;  between two others by taking the average between their line #'s.

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

Proc Closed Form3i()

;  Main procedure for managing data entry into form #3.

UseVars
   autolib        ;auto-load library

Private
   is_exiting,       ;whether the user wishes to exit from data entry
   is_form_picked,   ;whether the form has been picked at least once
   is_inserting,     ;whether the user is inserting a new master record
   is_locked         ;whether the master record is locked

;  Initialization

@ 3,0 ?? "Bringing the Vidorder table onto the workspace"
View "Vidorder"
is_form_picked = False     ;no form yet

;  Main data entry loop.
;  With each loop, the user selects a record to edit,
;  or presses [Ins] to add a new record.

While True
   is_exiting = False                  ;not exiting yet
   Select_Vidorder(3)                  ;let user select a Vidorder record
   If is_exiting then QuitLoop Endif   ;the user pressed [Esc] to quit
   Form3i_Master()                     ;handle master form data entry
EndWhile

;  All done

EndProc

WriteLib out_lib Form3i
Release Procs Form3i

;  ------------------------------------------------------------------

Proc Form3i_Master()

;  Processing of the master form

Private
   n_key_fields,        ;# of required key fields
   n_reqd_fields,       ;# of required fields
   prompt1,             ;the first line of the screen
   prompt2,             ;the second line of the screen
   reqd_fields,         ;array containing the required field names
   wait_key,            ;the key the user pressed in response to the Wait
   wait_msg             ;the message to display

;  Initialization

is_exiting = False               ;no attempt to exit yet
If is_inserting then             ;messages for inserting a new record
   prompt1 = "Add a new order"
   prompt2 = "Press [F2] when done, [Esc] to cancel, [F4] to enter films"
Else
   ImageRights Update            ;don't allow the user to change the key
   prompt1 = "Edit this order"
   prompt2 = "Press [F2] when done, [F4] to enter films"
Endif
wait_msg = ""                       ;no message

n_reqd_fields = 4                   ;# of required fields on the form
n_key_fields = 2                    ;# of required key fields on the form
Array reqd_fields[n_reqd_fields]    ;array of required field names
reqd_fields[1] = "Date"             ;list the required fields
reqd_fields[2] = "Customer #"
reqd_fields[3] = "Time"
reqd_fields[4] = "Store #"

;  Let the user edit the record

While True

   Wait Record
      Prompt prompt1,prompt2
      Message wait_msg
      Until "F2","F4","Esc",
            "LockKey","ReSyncKey","KeyLookup","Refresh","Dos","DosBig"

   wait_key = retval             ;remember the key the user pressed
   wait_msg = ""                 ;clear the message

;  Attempt to post a newly inserted record

   If is_inserting and not is_locked then    ;a brand new record
      Post_New_Vidorder()                    ;validate & lock the record
      If is_exiting then QuitLoop Endif      ;user pressed [Esc] to quit
      If not is_locked then Loop Endif       ;validation failed, try again
      prompt2 = "Press [F2] when done, [F4] to enter films"
   Endif

;  Respond to the user's keypress

   If wait_key<>"F2" and wait_key<>"F4" then    ;an illegal key
      Beep Loop                                 ;try again
   Endif
   Test_Reqd_Fields(n_reqd_fields)              ;test the required fields
   If not retval then
      Beep
      wait_msg = "You must fill in a "+field()
      Loop
   Endif
   If wait_key="F2" then QuitLoop Endif         ;finish up

;  [F4]:  Move to the embedded form

   MoveTo "KRental"                    ;move to the embedded form
   Form3i_Rental()                     ;process the rental form
   If is_exiting then QuitLoop Endif   ;all done
EndWhile

;  All done

ImageRights       ;restore full editing rights
Do_it!            ;end co-edit mode

EndProc

WriteLib out_lib Form3i_Master
Release Procs Form3i_Master

;  ------------------------------------------------------------------

Proc Form3i_Rental()

;  Processing of the embedded rental table form

Private
   above_line,          ;the line # above a new record
   below_line,          ;the line # below a new record
   between_line,        ;the new line #, between the line #'s above & below
   del_menu_sel,        ;user's response to the [Del] key confirmation menu
   is_arrival,          ;whether we're arriving at a new record
   is_bad_key,          ;whether an illegal key has been pressed
   is_del_key,          ;whether the delete key has been pressed
   is_exit_key,         ;whether the key will cause an exit from detail table
   is_first_arrival,    ;whether this is our first arrival at a new record
   is_ins_detail,       ;whether the detail record is a newly inserted record
   is_ins_down,         ;whether we pressed [Down] from the last record
   is_ins_key,          ;whether the insert key has been pressed
   is_locked_detail,    ;whether the detail record is locked
   prompt1,             ;the first line of the screen
   prompt2,             ;the second line of the screen
   wait_key             ;the key the user pressed in response to the Wait

;  Initialization

is_exiting = False               ;no attempt to exit yet
prompt1 = "Add or edit rental line items"
prompt2 = "Press [F2] when done, [F3] to return to master"
wait_msg = ""                    ;no message

is_arrival = True                ;we're arriving at a new KRental record
is_first_arrival = True          ;this is our first arrival

;  Let the user edit records

While True

;  Initialize a record when we first arrive at it.
;  If it's an existing record, then lock it

   If is_arrival then                     ;we've arrived at a new record
      If isblank([Film #]) then           ;no film #
         CtrlHome                         ;so move to it
      Endif
      If is_first_arrival then            ;we don't yet think we're inserting
         is_ins_detail = Is_New_KRental() ;did we arrive at a new record?
         If not is_ins_detail and isblank([Line #]) then    ;it's a blank
            Beep                          ;an dataError situation
            wait_msg = "There's a detail with a blank line #!"
            wait_key = ""
            QuitLoop                      ;get out of this table
         Endif
         If is_ins_detail then between_line=1 Endif   ;the first line #
         is_first_arrival = False         ;done with first arrival processing
      Endif
      is_locked_detail = False            ;it's not posted or locked yet
      If is_ins_detail then               ;we're inserting a record
         ImageRights                      ;give full rights to add the record
      Else                                ;we're updating a record
         LockRecord                       ;attempt to lock the record
         If not retval and errorcode()<>55 then   ;couldn't lock the record
            wait_msg = errormessage()     ;show the user the dataError message
            ImageRights ReadOnly          ;don't let them change the record
         Else                             ;we've successfully locked it
            is_locked_detail = True       ;it has been posted now
            ImageRights Update            ;don't let the user change the key
         Endif
      Endif
      is_arrival = False                  ;arrival is complete
   Endif

;  Wait for the user to fill in this record

   Wait Record
      Prompt prompt1,prompt2
      Message wait_msg
      Until "F2","F3","Ins","Del",
            "Up","Down","PgUp","PgDn","Home","End",
            "LockKey","ReSyncKey","KeyLookup","Refresh","Dos","DosBig"

   wait_key = retval             ;remember the key the user pressed
   wait_msg = ""                 ;clear the message

;  Classify the keystroke

   is_exit_key = (wait_key="F2" or wait_key="F3")
   is_bad_key  = (wait_key="LockKey" or      ;Alt-L
                  wait_key="ReSyncKey" or    ;Ctrl-L
                  wait_key="KeyLookup" or    ;Alt-K
                  wait_key="Refresh" or      ;Alt-R
                  wait_key="Dos" or          ;Ctrl-O
                  wait_key="DosBig")         ;Alt-O
   is_del_key  = (wait_key="Del")
   is_ins_key  = (wait_key="Ins")

;  Confirm the [Del] key

   If is_del_key and is_locked_detail then      ;[Del] on an old record
      Beep
      Message "Are you sure you want to delete this record?"
      ShowMenu
         "No":    "Do not delete this record",
         "Yes":   "Go ahead and delete this record"
      to del_menu_sel
      Message ""
      @ 1,0 Clear Eol
      @ 0,0 Clear Eol
      If del_menu_sel<>"Yes" then Loop Endif    ;[Del] wasn't confirmed
   Endif

;  Attempt to post a newly inserted record
   
   If is_ins_detail and not is_locked_detail then  ;a brand new record
      Post_New_KRental_I()                         ;validate & lock record
      If not is_locked_detail and not is_del_key then    ;locking failed
         Loop                                      ;try again
      Endif
   Endif

;  Disallow illegal keys.
;  Most of these are dangerous keys which the user should not press.

   If is_bad_key then            ;an illegal key
      Beep Loop                  ;ignore the key
   Endif

;  Post or delete the record

   ImageRights                   ;allow all activities to be performed

   is_ins_down = False           ;not yet a [Down] from the last record
   If is_del_key then            ;we're deleting a record
      is_ins_detail = is_ins_key or    ;we pressed [Ins] or
               (nimagerecords()<=1)    ;if we delete last record in table, 
                                       ;a new one will appear

;  If we re-insert a line at the same place we delete one,
;  we'll give the same line # as the deleted record

      If not isblank([Line #]) then    ;this record has a line #
         between_line = [Line #]       ;so save it for re-use
      Endif

      Del                              ;delete the record

;  When deleting a record, change the meaning of [Up] and [Down]

      If atlast() then           ;deleting the last record?
         If wait_key="Up"  then wait_key="" Endif        ;ignore [Up]
         If wait_key="Ins" then is_ins_down=True Endif   ;[Down] before [Ins]
      Else
         If wait_key="Down" then wait_key="" Endif       ;ignore [Up]
      Endif

;  Unlock an existing record

   Else
      UnLockRecord                     ;unlock the record
      is_ins_detail = is_ins_key       ;whether we're inserting next

;  Calculate the line # for a new record

      If is_ins_key then               ;we're about to insert a record
         below_line = [Line #]         ;remember the current line #
         If atfirst() then             ;we're at the first record
            Switch
               Case below_line>1.01: ;there's room for positive integer
                  between_line = int(below_line-1)  ;use a number one less
               Case below_line>0:    ;we can squeeze in a small fraction
                  between_line = below_line*0.5
               Otherwise:              ;it's zero or negative
                  between_line = below_line - 1
            EndSwitch
         Else                          ;we're not at the first record
            Skip -1                    ;move to the line above
            above_line = [Line #]      ;the line # from above
            Skip 1                     ;move back to the line below
            between_line = (below_line+above_line)*0.5   ;take the average
         Endif
      Endif
   Endif

;  Prepare for a new record

   If is_exit_key then           ;an exit key
      QuitLoop                   ;so we're all done here
   Endif

   If not isblank(wait_key) and wait_key<>"Del" then  
      If is_ins_down then        ;an [Ins] from a deleted last record
         Down                    ;open up the new record
      Endif
      is_ins_down = (wait_key="Down" and atlast())
      If is_ins_down then        ;we'll press down fromt he last record
         between_line = int([Line #]+1)   ;so increment line # by 1
      Endif
      KeyPress wait_key          ;perform the function
      If is_ins_down then        ;we pressed [Down] from the last record
         is_ins_detail = True    ;this inserts a new record
         Ins                     ;multi-user problem:  follow End-Down w/Ins
      Endif
   Endif

   is_arrival = True             ;flag for record arrival next time
EndWhile

;  All done

MoveTo "Vidorder"                ;return to the master record
is_exiting = (wait_key="F2")     ;exit the forms altogether

EndProc
WriteLib out_lib Form3i_Rental
Release Procs Form3i_Rental

;  ------------------------------------------------------------------

Proc Is_New_KRental_I()

;  This procedure tests whether we've arrived at a new, unposted
;  KRental detail record

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
If isblank([Line #]) and isblank([Film #]) and isblank([Rental Fee]) then
   between_line = 1                 ;assign #1 to the first line
   Return True                      ;we assume it's a new record
Endif
Return False                        ;otherwise, it's an old record

EndProc

WriteLib out_lib Is_New_KRental_I
Release Procs Is_New_KRental_I

;  ------------------------------------------------------------------

Proc Post_New_KRental_I()

;  When the user has entered a new record into the KRental table,
;  this procedure validates it and attempts to lock it.

;  Make sure the key field is filled in

If isblank([Film #]) then           ;it's blank
   is_del_key = True                ;delete the record
   Return
Endif

;  Assign the next available key to the new record

[Line #] = between_line             ;place the line number into the record

;  Attempt to lock the record.
;  Assume that failure to lock it means a key violation.

LockRecord                       ;attempt to post the record
If not retval then               ;we were unable to lock it
   Beep
   [Line #] = [Line #]           ;return to unposted state
   wait_msg = "This line # already exists in this order"
   Return
Endif

;  The record has been successfully locked

is_locked_detail = True       ;the record is now locked
ImageRights Update            ;don't let the user change the key anymore

;  All done

EndProc

WriteLib out_lib Post_New_KRental_I
Release Procs Post_New_KRental_I

;  ------------------------------------------------------------------


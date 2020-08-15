;  ------------------------------------------------------------------

;  This script contains the procedures to run forms #5 & #6.
;  These forms use a "shadow tables" technique to simulate a
;  form with more than five embedded tables.

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

Proc Closed Form5()

;  Main procedure for managing data entry into forms #5 & #6.

UseVars
   autolib        ;auto-load library

Private
   is_exiting,       ;whether the user wishes to exit from data entry
   is_inserting,     ;whether the user is inserting a new master record
   is_locked,        ;whether the master record is locked
   master_rec,       ;the master record, for copying to and from shadows
   shadow1,          ;name of the first shadow table
   shadow2           ;name of the second shadow table

;  Initialization

;  Create the shadow tables.
;  (An improvement here would be to only create the shadow tables
;  if they don't already exist.)

@ 3,0 ?? "Creating the shadow tables for Vidorder"
shadow1 = privdir()+"Vidord1"       ;we want them to be private
shadow2 = privdir()+"Vidord2"
Create shadow1 Like "Vidorder"      ;create the first shadow table
Form5_DeKey(shadow1)                ;we want unkeyed shadow tables
Menu {Tools} {Copy} {JustFamily}    ;we need forms, image sets, & val checks
   {Vidorder}
   Select shadow1
   {Replace}
Copy shadow1 shadow2                ;create the second shadow table
CopyForm shadow1 5 shadow1 "F"      ;make the first form the default
CopyForm shadow2 6 shadow2 "F"      ;make the second form the default

;  View the Vidorder table and the two shadow tables

@ 4,0 ?? "Bringing the Vidorder table onto the workspace"
ClearAll
View shadow1
View shadow2
View "Vidorder"

;  Main data entry loop.
;  With each loop, the user selects a record to edit,
;  or presses [Ins] to add a new record.

While True
   is_exiting = False                  ;not exiting yet
   Form5_Select()                      ;let user select a Vidorder record
   If is_exiting then QuitLoop Endif   ;the user pressed [Esc] to quit
   Form5_Master()                      ;handle master form data entry
EndWhile

;  All done

EndProc

WriteLib out_lib Form5
Release Procs Form5

;  -------------------------------------------------------------------

Proc Form5_DeKey(tablename)

;  This procedure removes the keys from the named table.

Private nkf       ;# of key fields in the table originally

;  Find out how many key fields and total fields there are

nkf = nkeyfields(tablename)      ;# of key fields now

;  Bring up the table structure for modification

Menu {Modify} {Restructure}      ;Restructure...
Select tablename                 ;... the table...
If menuchoice()<>"Error" then    ;uh-oh, "Cancel/Ok"!
   {Ok}                          ;... select "Ok"
Endif
MoveTo [Field Type]              ;prepare to process field types

;  Process the field type for the first "n" fields.
;  Remove "*" to de-key the field.

For i from 1 to nkf              ;do each key field
   Backspace                     ;remove the "*"
   Down                          ;move down to next field
EndFor                           ;do the next field

Do_It!               ;complete the restructure

EndProc              ;end of the procedure

WriteLib out_lib Form5_DeKey
Release Procs Form5_DeKey

;  ------------------------------------------------------------------

Proc Form5_Select()

;  Shows the user all the orders, and lets them select one to edit.

Private
   wait_key,      ;the key the user pressed in response to the Wait
   wait_msg       ;the message to show the user on the Wait

;  Initialization

wait_msg = "Which order do you want to view and edit?"

;  Let the user select an order

While True

   If sysmode()="CoEdit" then Do_it! Endif      ;get back to main mode
   MoveTo "Vidorder"                            ;return to the master table
   FirstShow         ;put the Vidorder table at the top of the screen
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
      CopyToArray master_rec  ;save the master record
   Endif

;  Copy the record to the two shadow tables

   MoveTo shadow1                   ;the first shadow table
   FormKey                          ;switch to the first shadow's form
   CtrlHome                         ;move back to the first field
   If is_inserting then             ;we're inserting into the master
      Ins                           ;so insert here as well
   Else                             ;we're updating an existing record
      CopyFromArray master_rec      ;so copy it into this shadow
   Endif

   QuitLoop                   ;exit to process this record   
EndWhile

;  All done

EndProc

WriteLib out_lib Form5_Select
Release Procs Form5_Select

;  ------------------------------------------------------------------

Proc Form5_Master()

;  Processing of the master form

Private
   n_key_fields,        ;# of required key fields
   n_reqd_fields,       ;# of required fields
   orig_cust,           ;original value of the customer # field
   orig_date,           ;original value of the date field
   prompt1,             ;the first line of the screen
   prompt2,             ;the second line of the screen
   reqd_fields,         ;array containing the required field names
   total_amt,           ;the total rental fee
   wait_key,            ;the key the user pressed in response to the Wait
   wait_msg             ;the message to display

;  Initialization

is_exiting = False               ;no attempt to exit yet
If is_inserting then             ;messages for inserting a new record
   prompt1 = "Add a new order"
   prompt2 = "Press [F2] when done, [Esc] to cancel, [F4] to enter films"
Else
   orig_date = [Date]   ;remember this field, because user shouldn't change it
   orig_cust = [Customer #]   ;remember this field too
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

;  Add up the current amount

If is_inserting then                ;this is a new record
   total_amt = 0                    ;so its amount starts out 0
Else                                ;we're editing a record
   MoveTo "Rental"                  ;switch to the rental form
   MoveTo [Rental Fee]              ;switch to the field to add up
   total_amt = imagecsum()          ;add up this column
   MoveTo shadow1                   ;return to the master form
Endif

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
      Form5_Post_New()                       ;validate & lock the record
      If is_exiting then QuitLoop Endif      ;user pressed [Esc] to quit
      If not is_locked then Loop Endif       ;validation failed, try again
      prompt2 = "Press [F2] when done, [F4] to enter films"
   Endif

;  Make sure the user has not illegally changed the key fields

   If is_locked then             ;we've already established a key
      Form5_Key_Changes()        ;make sure the user hasn't changed it
      If not retval then Loop Endif    ;there was a problem
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

   last_vidorder_field = field()       ;remember this field name
   MoveTo "Rental"                     ;move to the embedded form
   Form5_Rental()                      ;process the rental form
   If is_exiting then QuitLoop Endif   ;all done
EndWhile

;  All done

Do_it!                                 ;end co-edit mode
If isformview() then FormKey Endif     ;return to table view
MoveTo "Vidorder"                      ;return to the Vidorder table
ImageRights                            ;restore full editing rights

EndProc

WriteLib out_lib Form5_Master
Release Procs Form5_Master

;  ------------------------------------------------------------------

Proc Form5_Post_New()

;  When the user has entered a new record into the Vidorder table,
;  this procedure validates it and attempts to lock it.

;  Test for [Esc] to cancel

If wait_key="Esc" then                 ;the [Esc] key
   Del                                 ;remove the record from shadow table
   Do_it!                              ;only way out if table is empty
   If isformview() then FormKey Endif  ;switch to table view
   CoEditKey                           ;return to co-edit mode
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
;  This must be done in the Vidorder table, not the shadow table

CopyToArray master_rec     ;save the new record
UnlockRecord               ;unlock the shadow record
FormKey                    ;switch to table view
MoveTo "Vidorder"          ;return to the master table
CopyFromArray master_rec   ;copy the new record into Vidorder
LockRecord                 ;attempt to post the record
If not retval then         ;we were unable to lock it
   Beep
   Undo Undo               ;remove the record
   Ins                     ;re-insert it
   wait_msg = "This date/customer record already exists"
Else                       ;the record has been successfully locked
   is_locked = True        ;the record is now locked
   ImageRights Update      ;don't let the user change the key anymore
Endif

;  Return to the shadow table form

MoveTo shadow1          ;return to the shadow table
FormKey                 ;return to the form
If is_locked then       ;the lock succeeded
   LockRecord           ;so lock the shadow record
   orig_date = [Date]   ;remember this field, because user shouldn't change it
   orig_cust = [Customer #]   ;remember this field too
Else                    ;the lock failed
   MoveTo [Date]        ;so position the user to change the date
Endif

;  All done

EndProc

WriteLib out_lib Form5_Post_New
Release Procs Form5_Post_New

;  ------------------------------------------------------------------

Proc Form5_Key_Changes()

;  Tests whether the user has changed the key fields in the
;     master record of the shadow table.
;  The user is not allowed to do that.
;  If the shadow table is a keyed table, it's simpler to do an
;     "ImageRights Update" to prevent key field changes.
;  However, in this case the shadow table is unkeyed, and so
;     ImageRights Update would have no effect.
;  Therefore, we have to test each key field for changes.

Private
   keys_are_ok       ;whether the keys have not been changed

;  Test each of the fields

keys_are_ok = True                  ;no trouble so far
If [Date]<>orig_date then           ;an illegal key change
   MoveTo [Date]                    ;move to the key field
   [] = orig_date                   ;restore it to its original value
   keys_are_ok = False              ;set the trouble flag
Endif
If [Customer #]<>orig_cust then     ;an illegal key change
   MoveTo [Customer #]              ;move to the key field
   [] = orig_cust                   ;restore it to its original value
   keys_are_ok = False              ;set the trouble flag
Endif

If not keys_are_ok then             ;we found a changed key field
   Beep
   wait_msg = "You may not change the "+field()    ;error message
Endif

Return keys_are_ok

EndProc

WriteLib out_lib Form5_Key_Changes
Release Procs Form5_Key_Changes

;  ------------------------------------------------------------------

Proc Form5_Rental()

;  Processing of the embedded rental table form

Private
   change_amt,          ;amount to change the order total by
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
prompt2 = "Press [F2] when done, [F3] to return to master, [F4] to see films"
wait_msg = ""                    ;no message

is_arrival = True                ;we're arriving at a new Rental record
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
         is_ins_detail = Is_New_Rental()  ;did we arrive at a new record?
         is_first_arrival = False         ;done with first arrival processing
      Endif
      is_locked_detail = False            ;it's not posted or locked yet
      If is_ins_detail then               ;we're inserting a record
         ImageRights                      ;give full rights to add the record
      Else                                ;we're updating a record
         LockRecord                       ;attempt to lock the record
         If not retval and errorcode()<>55 then   ;couldn't lock the record
            wait_msg = errormessage()     ;show the user the error message
            ImageRights ReadOnly          ;don't let them change the record
         Else                             ;we've successfully locked it
            is_locked_detail = True       ;it has been posted now
            ImageRights Update            ;don't let the user change the key
         Endif
      Endif
      change_amt = -[Rental Fee]          ;remember original fee on this line
      is_arrival = False                  ;arrival is complete
   Endif

;  Wait for the user to fill in this record

   Wait Record
      Prompt prompt1,prompt2
      Message wait_msg
      Until "F2","F3","F4","Ins","Del",
            "Up","Down","PgUp","PgDn","Home","End",
            "LockKey","ReSyncKey","KeyLookup","Refresh","Dos","DosBig"

   wait_key = retval             ;remember the key the user pressed
   wait_msg = ""                 ;clear the message

;  Classify the keystroke

   is_exit_key = (wait_key="F2" or wait_key="F3" or wait_key="F4")
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
      Post_New_Rental()                            ;validate & lock record
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
      Del                        ;delete the record

;  When deleting a record, change the meaning of [Up] and [Down]

      If atlast() then           ;deleting the last record?
         If wait_key="Up"  then wait_key="" Endif        ;ignore [Up]
         If wait_key="Ins" then is_ins_down=True Endif   ;[Down] before [Ins]
      Else
         If wait_key="Down" then wait_key="" Endif       ;ignore [Up]
      Endif

;  Unlock an existing record

   Else
      change_amt = change_amt + [Rental Fee]   ;change the total
      UnLockRecord                             ;unlock the record
      is_ins_detail = is_ins_key               ;whether we're inserting next
   Endif

;  Update the amount total, and prepare for a new record

   If change_amt<>0 then                     ;there has been a change
      total_amt = total_amt + change_amt     ;new amount
   Endif

   If is_exit_key then           ;an exit key
      QuitLoop                   ;now we're all done here
   Endif

   If not isblank(wait_key) and wait_key<>"Del" then  
      If is_ins_down then        ;an [Ins] from a deleted last record
         Down                    ;open up the new record
      Endif
      is_ins_down = (wait_key="Down" and atlast())
      KeyPress wait_key          ;perform the function
      If is_ins_down then        ;we pressed [Down] from the last record
         is_ins_detail = True    ;this inserts a new record
         Ins                     ;multi-user problem:  follow End-Down w/Ins
      Endif
   Endif

   is_arrival = True             ;flag for record arrival next time
EndWhile

;  Figure out where to move to next

If wait_key="F4" then                        ;[F4] means display films
   Form5_Films()                             ;so display them
   If is_exiting then wait_key="F2" Endif    ;user's pressed [F2]
Endif

MoveTo shadow1                      ;return to the master record
MoveTo Field last_vidorder_field    ;return to the same field

is_exiting = (wait_key="F2")        ;exit the forms altogether

EndProc

WriteLib out_lib Form5_Rental
Release Procs Form5_Rental

;  ------------------------------------------------------------------

Proc Form5_Films()

;  Processing of the embedded Films table form

Private
   wait_key             ;the key the user pressed in response to the Wait

;  Initialization

MoveTo "Films"          ;move to the form
wait_msg = ""           ;no error message yet

;  Let the user view and edit the entire table

While True

   Wait Table
      Prompt "View and edit the films inventory",
             "Press [F2] to quit, [F3] to return to the master form," +
             " [F4] for Payments"
      Message wait_msg
      Until "F2","F3","F4"

   wait_key = retval             ;remember the key the user pressed
   wait_msg = ""                 ;clear the message

;  Before exiting, test for a key violation

   LockRecord                    ;try to lock the record
   If not retval then            ;we couldn't lock it
      If errorcode()=53 then     ;a key violation
         Beep
         wait_msg = "This film # already exists"
         MoveTo [Film #]         ;return to the problem field
         [] = []                 ;return the problem record to insert status
         Loop                    ;go back and try again
      Endif
   Endif
   UnlockRecord                  ;unlock the record

   QuitLoop                      ;all done
EndWhile

;  Determine where to go from here

If wait_key="F4" then            ;[F4] means move forward
   Form5_Payments()              ;so move forward to the payments form
   If is_exiting then wait_key="F2" Endif    ;user's pressed [F2]
Endif

is_exiting = (wait_key="F2")     ;exit the forms altogether

;  All done

EndProc

WriteLib out_lib Form5_Films
Release Procs Form5_Films

;  ------------------------------------------------------------------

Proc Form5_Payments()

;  Processing of the embedded Payments table form

Private
   change_amt,          ;amount to change the order total by
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
   tax_rate,            ;the tax rate from the table
   total_pmt,           ;total payments
   wait_key             ;the key the user pressed in response to the Wait

;  Initialization

;  Switch to the other shadow table

@ 1,0 Clear Eol
@ 0,0 Clear Eol ?? "One Moment Please"
Style Blink ?? "..." Style

MoveTo shadow1             ;move to the master form
CopyToArray master_rec     ;save the master record
UnLockRecord               ;unlock the record so Paradox will let us depart
FormKey                    ;switch to table view
MoveTo shadow2             ;move to the other shadow table
FormKey                    ;switch to the form
CopyFromArray master_rec   ;transfer the record
LockRecord                 ;lock this shadow record

;  Calculate the tax rate

Form5_Tax_Rate()           ;calculate the tax rate

;  Calculate the payments

MoveTo "Payment"           ;move to the payment table
MoveTo [Amount of Payment] ;move to the amount column
total_pmt = imagecsum()    ;the total payment

;  Update the order amounts

MoveTo "Amount"                     ;move to the amount table
[Total Rental Fee] = total_amt      ;sum of rentals, from the Rental form
[Sales Tax Rate]   = tax_rate       ;tax rate, from earlier calculation
[Sales Tax Amount] = round(total_amt*tax_rate*.01,2)    ;tax amount
[Total Order]      = total_amt + [Sales Tax Amount]
[Amount Paid]      = total_pmt
[Balance Due]      = [Total Order] - total_pmt
If abs([Balance Due])<0.001 then [Balance Due]=0 Endif   ;rounding error

;  Initialize the payment record

MoveTo "Payment"
is_exiting = False               ;no attempt to exit yet
prompt1 = "Add or edit payments"
prompt2 = "Press [F2] when done, [F3] to return to master, " +
                 "[F4] to see store & employees"
wait_msg = ""                    ;no message

is_arrival = True                ;we're arriving at a new Payment record
is_first_arrival = True          ;this is our first arrival

;  Let the user edit records

While True

;  Initialize a record when we first arrive at it.
;  If it's an existing record, then lock it

   If is_arrival then                        ;we've arrived at a new record
      If isblank([Method of Payment]) then   ;key field is blank
         CtrlHome                            ;so move to it
      Endif
      If is_first_arrival then            ;we don't yet think we're inserting
         is_ins_detail = Is_New_Payment() ;did we arrive at a new record?
         is_first_arrival = False         ;done with first arrival processing
      Endif
      is_locked_detail = False            ;it's not posted or locked yet
      If is_ins_detail then               ;we're inserting a record
         ImageRights                      ;give full rights to add the record
      Else                                ;we're updating a record
         LockRecord                       ;attempt to lock the record
         If not retval and errorcode()<>55 then   ;couldn't lock the record
            wait_msg = errormessage()     ;show the user the error message
            ImageRights ReadOnly          ;don't let them change the record
         Else                             ;we've successfully locked it
            is_locked_detail = True       ;it has been posted now
            ImageRights Update            ;don't let the user change the key
         Endif
      Endif
      change_amt = -[Amount of Payment]   ;remember original amt on this line
      is_arrival = False                  ;arrival is complete
   Endif

;  Wait for the user to fill in this record

   Wait Record
      Prompt prompt1,prompt2
      Message wait_msg
      Until "F2","F3","F4","Ins","Del",
            "Up","Down","PgUp","PgDn","Home","End",
            "LockKey","ReSyncKey","KeyLookup","Refresh","Dos","DosBig"

   wait_key = retval             ;remember the key the user pressed
   wait_msg = ""                 ;clear the message

;  Classify the keystroke

   is_exit_key = (wait_key="F2" or wait_key="F3" or wait_key="F4")
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
      Post_New_Payment()                           ;validate & lock record
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
      Del                        ;delete the record

;  When deleting a record, change the meaning of [Up] and [Down]

      If atlast() then           ;deleting the last record?
         If wait_key="Up"  then wait_key="" Endif        ;ignore [Up]
         If wait_key="Ins" then is_ins_down=True Endif   ;[Down] before [Ins]
      Else
         If wait_key="Down" then wait_key="" Endif       ;ignore [Up]
      Endif

;  Unlock an existing record

   Else
      change_amt = change_amt + [Amount of Payment]   ;change the total
      UnLockRecord                             ;unlock the record
      is_ins_detail = is_ins_key               ;whether we're inserting next
   Endif

;  Update the amount total, and prepare for a new record

   If change_amt<>0 then                     ;there has been a change
      total_pmt = total_pmt + change_amt     ;new amount
      MoveTo "Amount"                        ;switch to the amount record
      [Amount Paid] = total_pmt              ;update amount record
      [Balance Due] = [Total Order] - total_pmt
      If abs([Balance Due])<0.001 then [Balance Due]=0 Endif
      MoveTo "Payment"                       ;return to detail record
   Endif

   If is_exit_key then           ;an exit key
      QuitLoop                   ;now we're all done here
   Endif

   If not isblank(wait_key) and wait_key<>"Del" then  
      If is_ins_down then        ;an [Ins] from a deleted last record
         Down                    ;open up the new record
      Endif
      is_ins_down = (wait_key="Down" and atlast())
      KeyPress wait_key          ;perform the function
      If is_ins_down then        ;we pressed [Down] from the last record
         is_ins_detail = True    ;this inserts a new record
         Ins                     ;multi-user problem:  follow End-Down w/Ins
      Endif
   Endif

   is_arrival = True             ;flag for record arrival next time
EndWhile

;  Figure out where to move next

If wait_key="F4" then                        ;[F4] means move to next table
   Form5_Employee()                          ;so display it
   If is_exiting then wait_key="F2" Endif    ;user pressed [F2]
Endif

;  Return to the other shadow table

@ 1,0 Clear Eol
@ 0,0 Clear Eol ?? "One Moment Please"
Style Blink ?? "..." Style

MoveTo shadow2                   ;move to the master form
UnlockRecord                     ;unlock the record
FormKey                          ;switch to table view
MoveTo shadow1                   ;move to the first shadow table
FormKey                          ;restore the form
LockRecord                       ;re-lock the record
is_exiting = (wait_key="F2")     ;exit the forms altogether

;  All done

EndProc

WriteLib out_lib Form5_Payments
Release Procs Form5_Payments

;  ------------------------------------------------------------------

Proc Form5_Tax_Rate()

;  This procedure calculates the sales tax rate for a Vidorder record

Private
   store_state,      ;the state where the store is located
   customer_state    ;the state where the customer lives

;  Get the key to the tax rate table

store_state    = [Store State]
customer_state = [Customer State]

;  Look up the tax rate record

MoveTo "TaxRate"                    ;move to the table
Locate store_state,customer_state   ;look for the record
If retval then                      ;we found the record
   tax_rate = [Sales Tax Rate]      ;so get the tax rate
Else                                ;could not find the record
   tax_rate = blanknum()            ;so make it blank
Endif

;  All done

EndProc

WriteLib out_lib Form5_Tax_Rate
Release Procs Form5_Tax_Rate

;  ------------------------------------------------------------------

Proc Is_New_Payment()

;  This procedure tests whether we've arrived at a new, unposted
;  payment detail record

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
If isblank([Method of Payment]) and 
   isblank([Amount of Payment]) then      ;these fields blank
      Return True                         ;we assume it's a new record
Endif
Return False                        ;otherwise, it's an old record

EndProc

WriteLib out_lib Is_New_Payment
Release Procs Is_New_Payment

;  ------------------------------------------------------------------

Proc Post_New_Payment()

;  When the user has entered a new record into the Payment table,
;  this procedure validates it and attempts to lock it.

;  Make sure the key field is filled in

If isblank([Method of Payment]) then   ;it's blank
   is_del_key = True                   ;delete the record
   Return
Endif

;  Attempt to lock the record.
;  Assume that failure to lock it means a key violation.

LockRecord           ;attempt to post the record
If not retval then   ;we were unable to lock it
   Beep
   MoveTo [Method of Payment]
   [] = []           ;return to unposted state
   wait_msg = "You may not repeat the same method of payment twice"
   Return
Endif

;  The record has been successfully locked

is_locked_detail = True       ;the record is now locked
ImageRights Update            ;don't let the user change the key anymore

;  All done

EndProc

WriteLib out_lib Post_New_Payment
Release Procs Post_New_Payment

;  ------------------------------------------------------------------

Proc Form5_Employee()

;  Processing of the embedded employee table form

Private
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

MoveTo "Employee"
is_exiting = False               ;no attempt to exit yet
prompt1 = "Add or edit employee list for this store"
prompt2 = "Press [F2] when done, [F3] to return to master"
wait_msg = ""                    ;no message

is_arrival = True                ;we're arriving at a new Employee record
is_first_arrival = True          ;this is our first arrival

;  Let the user edit records

While True

;  Initialize a record when we first arrive at it.
;  If it's an existing record, then lock it

   If is_arrival then                     ;we've arrived at a new record
      If isblank([Employee #]) then       ;the key is not filled in
         CtrlHome                         ;so move to it
      Endif
      If is_first_arrival then               ;we don't yet think it's new
         is_ins_detail = Is_New_Employee()   ;did we arrive at a new record?
         is_first_arrival = False            ;done with 1st arrival process
      Endif
      is_locked_detail = False            ;it's not posted or locked yet
      If is_ins_detail then               ;we're inserting a record
         ImageRights                      ;give full rights to add the record
      Else                                ;we're updating a record
         LockRecord                       ;attempt to lock the record
         If not retval and errorcode()<>55 then   ;couldn't lock the record
            wait_msg = errormessage()     ;show the user the error message
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
      Post_New_Employee()                          ;validate & lock record
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
      Del                        ;delete the record

;  When deleting a record, change the meaning of [Up] and [Down]

      If atlast() then           ;deleting the last record?
         If wait_key="Up"  then wait_key="" Endif        ;ignore [Up]
         If wait_key="Ins" then is_ins_down=True Endif   ;[Down] before [Ins]
      Else
         If wait_key="Down" then wait_key="" Endif       ;ignore [Up]
      Endif

;  Unlock an existing record

   Else
      UnLockRecord                             ;unlock the record
      is_ins_detail = is_ins_key               ;whether we're inserting next
   Endif

;  Prepare for a new record

   If is_exit_key then           ;an exit key
      QuitLoop                   ;now we're all done here
   Endif

   If not isblank(wait_key) and wait_key<>"Del" then  
      If is_ins_down then        ;an [Ins] from a deleted last record
         Down                    ;open up the new record
      Endif
      is_ins_down = (wait_key="Down" and atlast())
      KeyPress wait_key          ;perform the function
      If is_ins_down then        ;we pressed [Down] from the last record
         is_ins_detail = True    ;this inserts a new record
         Ins                     ;multi-user problem:  follow End-Down w/Ins
      Endif
   Endif

   is_arrival = True             ;flag for record arrival next time
EndWhile

;  Return to the previous forms

is_exiting = (wait_key="F2")        ;exit the forms altogether

EndProc

WriteLib out_lib Form5_Employee
Release Procs Form5_Employee

;  ------------------------------------------------------------------

Proc Is_New_Employee()

;  This procedure tests whether we've arrived at a new, unposted
;  employee detail record

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
If isblank([Employee #]) and 
   isblank([Employee Name]) then    ;these fields blank
      Return True                   ;we assume it's a new record
Endif
Return False                        ;otherwise, it's an old record

EndProc

WriteLib out_lib Is_New_Employee
Release Procs Is_New_Employee

;  ------------------------------------------------------------------

Proc Post_New_Employee()

;  When the user has entered a new record into the Employee table,
;  this procedure validates it and attempts to lock it.

;  Make sure the key field is filled in

If isblank([Employee #]) then          ;it's blank
   is_del_key = True                   ;delete the record
   Return
Endif

;  Attempt to lock the record.
;  Assume that failure to lock it means a key violation.

LockRecord           ;attempt to post the record
If not retval then   ;we were unable to lock it
   Beep
   MoveTo [Employee #]
   [] = []           ;return to unposted state
   wait_msg = "This employee # is already used in another record"
   Return
Endif

;  The record has been successfully locked

is_locked_detail = True       ;the record is now locked
ImageRights Update            ;don't let the user change the key anymore

;  All done

EndProc

WriteLib out_lib Post_New_Employee
Release Procs Post_New_Employee

;  ------------------------------------------------------------------


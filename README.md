TasKoord
========

CS2103 project of team t16-1j
 
This is a classical ToDO list manager based on command line Input 

Keys and Formats:

Description - any words between the command word and the next key
Start time - after “from”
end time - after “to”
deadline time - after “by”
date - after “on”
location - words after “at”

A task is identified by its description, time and location or by number

Commands:

ADD
add timedTaskA from 3pm to 4pm on 15 nov 2014 at locationA
add timedTaskB from 0500h to 0600h on 15/11/14 at locationB
add deadlineTaskA by 8pm on 16/11/14 at locationC
add floatingTaskA
add timedTaskC from 3pm on 17 nov 2014 to 1600h on 18/11/14 at locationC
add timedTaskD on 18 Nov 2014 at locationD
add travel /from home /to school /on a bus /by PIE on 14 nov 2014 at Garden /by the bay
add timedTaskE from 1500h to 1600h at locationE
add timedTaskF from 20 Nov 2014 to 21 Nov 2014 at location F
add deadlineTaskB by 21 Nov 2014

EDIT
edit timedTaskA correct from 4pm to 5pm on 15/11/14
edit timedTaskB correct timedTaskC from 4pm to 5pm on 15/11/14
edit timedTaskD correct deadlineTaskA by 3pm on 15/11/14
edit 1 correct timedTaskB
edit 2 correct from 3pm to 4pm
edit 3 correct by 5pm
edit 4 correct on 14/11/14
edit 5 correct from 3pm to 4pm on 14/11/14
edit 6 correct by 5pm on 14/11/14
edit 7 correct priority low (alternative to set)
edit 8 correct status completed (alternative to status)

SET
set 1 priority high
set 2 priority low
set 2 priority medium
set 1 status discarded
set 2 status completed
set 2 status pending

CLEAR/DELETE
clear
delete <number>
delete <task identification>

UNDO/REDO
undo
redo

LIST
list
list on 01/11/14
list from 1pm on 15 Nov 2014 to 1pm on 16 Nov 2014
list from 1 jan 2014 to 31 dec 2014
list status completed
list status pending
list status discarded
list priority high (does not show discarded/completed task)
list priority medium (does not show discarded/completed task)
list priority low (does not show discarded/completed task)
list events
list deadlines 
list floating
list <a specification> on <date>
list events and deadlines
list events and deadlines and floating
list upcoming deadlines priority high status completed
list upcoming <5 days/deadlines/events>

SEARCH
search <keyword>

HELP
help

SYNC
sync

EXIT
exit

Exceptions handled
list (when storage is empty)
add (or commands without the required detail)
search
add taskWrong to 4pm
add wrongTime from 15am to 16am 
add wrongDate from 30/15/2017

More features
use “up” and “down” buttons to accessed keyed commands

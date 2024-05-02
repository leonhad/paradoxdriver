select count(*)
from geog.tblAC ac
         cross join geog.tblsttes st
         join geog.County c
              on c.StateID = st.State
where st.State = ac.State

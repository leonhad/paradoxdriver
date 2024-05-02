select ac.AreaCode, ac.AreasCovered, st.State, c.CountyID, c.County
from geog.tblAC ac
         cross join geog.tblsttes st
         join geog.County c
              on c.StateID = st.State
where st.State = ac.State
order by st.State, ac.AreaCode

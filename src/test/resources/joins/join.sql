select a.ID as ID_A, b.Id as ID_B
from joins.joina a
         inner join joins.joinb b on a.ID = b.Id

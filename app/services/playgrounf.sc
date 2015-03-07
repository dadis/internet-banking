case class rec(a:Int,b:Int,c:Int)
case class month(data:List[rec])
val l = List(rec(1,2014,5),rec(2,2014,5),rec(1,2014,6),rec(2,2014,6))

val x = l.groupBy(m => m.c +"-"+m.b).map(li => month(li._2)).toList



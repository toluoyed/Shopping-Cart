package exercises

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.{Greater, Less}
import monix.newtypes.NewtypeWrapped

type AgePred = Int Refined (Greater[0] And Less[150])

type Age = Age.Type
object Age extends NewtypeWrapped[AgePred]

def isAdult(age: Age): Boolean =
  age.value.value >= 18
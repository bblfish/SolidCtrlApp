package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* import warning: transforms.RemoveMultipleInheritance#findNewParents newComments Dropped parents 
- n3js.rdfjsTypes.dataModelMod._Term because Already inherited
- n3js.rdfjsTypes.dataModelMod.BaseQuad because Already inherited
- n3js.rdfjsTypes.dataModelMod.Quad because var conflicts: graph, `object`, predicate, subject, termType, value. Inlined subject_Quad, predicate_Quad, object_Quad, graph_Quad */ @JSImport("n3", "Quad")
@js.native
class Quad protected ()
  extends BaseQuad
     with n3js.rdfjsTypes.dataModelMod._QuadObject
     with n3js.rdfjsTypes.dataModelMod._QuadSubject {
  def this(subject: Term, predicate: Term, `object`: Term) = this()
  def this(subject: Term, predicate: Term, `object`: Term, graph: Term) = this()
  
  /**
    * The named graph.
    * @see Quad_Graph
    */
  @JSName("graph")
  var graph_Quad: n3js.rdfjsTypes.dataModelMod.QuadGraph = js.native
  
  /**
    * The object.
    * @see Quad_Object
    */
  @JSName("object")
  var object_Quad: n3js.rdfjsTypes.dataModelMod.QuadObject = js.native
  
  /**
    * The predicate.
    * @see Quad_Predicate
    */
  @JSName("predicate")
  var predicate_Quad: n3js.rdfjsTypes.dataModelMod.QuadPredicate = js.native
  
  /**
    * The subject.
    * @see Quad_Subject
    */
  @JSName("subject")
  var subject_Quad: n3js.rdfjsTypes.dataModelMod.QuadSubject = js.native
}

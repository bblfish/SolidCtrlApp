package n3js.n3.mod

import n3js.rdfjsTypes.datasetMod.DatasetCore
import n3js.rdfjsTypes.streamMod.Stream
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "Store")
@js.native
class Store[Q_RDF /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */, Q_N3 /* <: BaseQuad */, OutQuad /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */, InQuad /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */] ()
  extends StObject
     with n3js.rdfjsTypes.streamMod.Store[Q_RDF]
     with DatasetCore[OutQuad, InQuad] {
  def this(triples: js.Array[Q_RDF]) = this()
  def this(triples: js.Array[Q_RDF], options: StoreOptions) = this()
  def this(triples: Unit, options: StoreOptions) = this()
  
  def addQuad(quad: Q_RDF): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any,
    done: js.Function0[Unit]
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: Unit,
    done: js.Function0[Unit]
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ]
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any,
    done: js.Function0[Unit]
  ): Unit = js.native
  def addQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: Unit,
    done: js.Function0[Unit]
  ): Unit = js.native
  
  def addQuads(quads: js.Array[Q_RDF]): Unit = js.native
  
  def countQuads(subject: OTerm, predicate: OTerm, `object`: OTerm, graph: OTerm): Double = js.native
  
  def createBlankNode(): BlankNode = js.native
  def createBlankNode(suggestedName: String): BlankNode = js.native
  
  def every(callback: QuadPredicate[Q_N3], subject: OTerm, predicate: OTerm, `object`: OTerm, graph: OTerm): Boolean = js.native
  
  def forEach(callback: QuadCallback[Q_N3], subject: OTerm, predicate: OTerm, `object`: OTerm, graph: OTerm): Unit = js.native
  
  @JSName("forGraphs")
  def forGraphs_graph(
    callback: js.Function1[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['graph'] */ /* result */ js.Any, 
      Unit
    ],
    subject: OTerm,
    predicate: OTerm,
    `object`: OTerm
  ): Unit = js.native
  
  @JSName("forObjects")
  def forObjects_object(
    callback: js.Function1[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['object'] */ /* result */ js.Any, 
      Unit
    ],
    subject: OTerm,
    predicate: OTerm,
    graph: OTerm
  ): Unit = js.native
  
  @JSName("forPredicates")
  def forPredicates_predicate(
    callback: js.Function1[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['predicate'] */ /* result */ js.Any, 
      Unit
    ],
    subject: OTerm,
    `object`: OTerm,
    graph: OTerm
  ): Unit = js.native
  
  @JSName("forSubjects")
  def forSubjects_subject(
    callback: js.Function1[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['subject'] */ /* result */ js.Any, 
      Unit
    ],
    predicate: OTerm,
    `object`: OTerm,
    graph: OTerm
  ): Unit = js.native
  
  def getGraphs(subject: OTerm, predicate: OTerm, `object`: OTerm): js.Array[
    /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['graph'] */ js.Any
  ] = js.native
  
  def getObjects(subject: OTerm, predicate: OTerm, graph: OTerm): js.Array[
    /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['object'] */ js.Any
  ] = js.native
  
  def getPredicates(subject: OTerm, `object`: OTerm, graph: OTerm): js.Array[
    /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['predicate'] */ js.Any
  ] = js.native
  
  def getQuads(subject: OTerm, predicate: OTerm, `object`: js.Array[OTerm], graph: OTerm): js.Array[Quad] = js.native
  def getQuads(subject: OTerm, predicate: OTerm, `object`: OTerm, graph: OTerm): js.Array[Quad] = js.native
  
  def getSubjects(predicate: OTerm, `object`: OTerm, graph: OTerm): js.Array[
    /* import warning: importer.ImportType#apply Failed type conversion: Q_N3['subject'] */ js.Any
  ] = js.native
  
  /**
    * Returns a new dataset that is comprised of all quads in the current instance matching the given arguments.
    *
    * The logic described in {@link https://rdf.js.org/dataset-spec/#quad-matching|Quad Matching} is applied for each
    * quad in this dataset to check if it should be included in the output dataset.
    *
    * This method always returns a new DatasetCore, even if that dataset contains no quads.
    *
    * Since a `DatasetCore` is an unordered set, the order of the quads within the returned sequence is arbitrary.
    *
    * @param subject   The optional exact subject to match.
    * @param predicate The optional exact predicate to match.
    * @param object    The optional exact object to match.
    * @param graph     The optional exact graph to match.
    */
  /* InferMemberOverrides */
  override def `match`(
    subject: js.UndefOr[n3js.rdfjsTypes.dataModelMod.Term | Null],
    predicate: js.UndefOr[n3js.rdfjsTypes.dataModelMod.Term | Null],
    `object`: js.UndefOr[n3js.rdfjsTypes.dataModelMod.Term | Null],
    graph: js.UndefOr[n3js.rdfjsTypes.dataModelMod.Term | Null]
  ): (DatasetCore[OutQuad, InQuad]) with Stream[Q_RDF] = js.native
  
  def removeQuad(quad: Q_RDF): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any,
    done: js.Function0[Unit]
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any,
    graph: Unit,
    done: js.Function0[Unit]
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ]
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['graph'] */ js.Any,
    done: js.Function0[Unit]
  ): Unit = js.native
  def removeQuad(
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['predicate'] */ js.Any,
    `object`: js.Array[
      /* import warning: importer.ImportType#apply Failed type conversion: Q_RDF['object'] */ js.Any
    ],
    graph: Unit,
    done: js.Function0[Unit]
  ): Unit = js.native
  
  def removeQuads(quads: js.Array[Q_RDF]): Unit = js.native
  
  def some(callback: QuadPredicate[Q_N3], subject: OTerm, predicate: OTerm, `object`: OTerm, graph: OTerm): Boolean = js.native
}

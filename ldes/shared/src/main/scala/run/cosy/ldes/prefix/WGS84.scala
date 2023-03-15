package run.cosy.ldes.prefix

import org.w3.banana.{Ops, PrefixBuilder, RDF}

object WGS84:
  def apply[R <: RDF](using Ops[R]) = new WGS84()

class WGS84[R <: RDF](using ops: Ops[R])
  extends PrefixBuilder[R]("wgs84", ops.URI("http://www.w3.org/2003/01/geo/wgs84_pos#")):

  lazy val Point = apply("Point")
  lazy val SpatialThing = apply("SpatialThing")
  lazy val alt = apply("alt")
  lazy val lat = apply("lat")
  lazy val lat_long = apply("lat_long")
  lazy val location = apply("location")

package run.cosy.ld

import org.w3.banana.jena.JenaRdf.{R, given}


class JenaFoafWebTest extends FoafWebTest[R]()

class JenaLdesSimpleWebTest extends LdesSimpleWebTest[R]()

class JenaLdesBrokenWebTest extends LdesBrokenWebTest[R]()



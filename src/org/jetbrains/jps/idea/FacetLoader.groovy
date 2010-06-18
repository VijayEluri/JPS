package org.jetbrains.jps.idea

import org.jetbrains.jps.Module

/**
 * @author nik
 */
class FacetLoader {
  private final Module module
  private final String moduleBasePath
  IdeaProjectLoader loader
  private static final Map<String, String> OUTPUT_PATHS = [
          "web.xml": "WEB-INF",
          "ejb-jar.xml": "META-INF",
          "application.xml": "META-INF",
          "context.xml": "META-INF"
  ]

  def FacetLoader(IdeaProjectLoader loader, Module module, String moduleBasePath) {
    this.loader = loader
    this.module = module
    this.moduleBasePath = moduleBasePath
  }


  def loadFacets(Node facetManagerTag) {
    def javaeeTypes = ["web", "ejb", "javaeeApplication"] as Set
    facetManagerTag.facet.each {Node facetTag ->
      def type = facetTag."@type"
      if (type in javaeeTypes) {
        def facet = new JavaeeFacet(name: facetTag."@name")
        facetTag.configuration?.descriptors?.deploymentDescriptor?.each {Node tag ->
          def outputPath = OUTPUT_PATHS[tag."@name"]
          if (outputPath == null) {
            outputPath = type == "web" ? "WEB-INF" : "META-INF"
          }
          facet.descriptors << [path: urlToPath(tag."@url"), outputPath: outputPath]
        }
        facetTag.configuration?.webroots?.root?.each {Node tag ->
          facet.webRoots << [path: urlToPath(tag."@url"), outputPath: tag."@relative"]
        }
        def id = "${module.name}/$type/${facet.name}"
        module.facets[id] = facet;
      }
    }
  }

  def urlToPath(String url) {
    return loader.expandMacro(IdeaProjectLoader.pathFromUrl(url), moduleBasePath)
  }
}

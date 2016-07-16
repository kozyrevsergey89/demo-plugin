package com.uamobitech.custom.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class ExamplePlugin implements Plugin<Project> {

  void apply(Project project) {

    project.configure(project) {
      if(it.hasProperty("android")) {
        tasks.whenTaskAdded { theTask ->
          // Returns an empty list if the plugin only has the default flavor
          // But we still need something to iterate over, so let’s make an empty flavor.
          def projectFlavorNames = project.("android").productFlavors.collect { it.name }
          projectFlavorNames = projectFlavorNames.size() != 0 ? projectFlavorNames : [""]

          project.("android").buildTypes.all { build ->
            def buildName = build.name
            def flavorPath
            for (flavorName in projectFlavorNames) {
              if (!"".equals(flavorName)) {
                flavorPath = "${flavorName}/${buildName}"
              } else {
                // If we are working with the empty flavor, there’s no second folder
                flavorPath = "${buildName}"
              }
              def manifestPath = "app/build/intermediates/manifests/full/${flavorPath}/AndroidManifest.xml"
              def taskAffix
              if (!"".equals(flavorName)) {
                taskAffix = "${flavorName.capitalize()}${buildName.capitalize()}"
              } else {
                // If we are working with the empty flavor, there’s no second affix
                taskAffix = "${buildName.capitalize()}"
              }
              def compileTask = "compile${taskAffix}".toString()
              if(theTask.name.toString().equals('checkDebugManifest')) {

                def yourTaskName = "Manifest${taskAffix}"
                project.task(yourTaskName) << {
                  description = 'Outputs the manifest file size'
                  def manifest = new File(manifestPath)
                  logger.warn("***Hello UAMOBITECH!*** Manifest Path: " + manifestPath)
                  logger.warn("***Hello UAMOBITECH!*** Manifest Size: " + manifest.length())
                }
                theTask.dependsOn(yourTaskName)
              }
            }
          }
        }
      }
    }
  }
}

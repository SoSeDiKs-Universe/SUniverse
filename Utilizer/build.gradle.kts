description = "Shared dumping ground for SoSeDiK's witchery"

dependencies {
    paperLibrary("org.incendo:cloud-paper:${project.property("cloudImplVersion")}")
    paperLibrary("org.incendo:cloud-annotations:${project.property("cloudVersion")}")
}

paper {
    name = "Utilizer"
    main = "me.sosedik.utilizer.Utilizer"
    loader = "me.sosedik.utilizer.PaperPluginLibrariesLoader"
    generateLibrariesJson = true
}

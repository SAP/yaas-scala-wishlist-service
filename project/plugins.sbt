// The Typesafe repository

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.3")

// Eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

// Scalastyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")

// Scapegoat
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.4")
addSbtPlugin("org.foundweekends" % "sbt-bintray"           % "0.5.1")
addSbtPlugin("io.get-coursier"   % "sbt-coursier"          % "1.0.0-RC13")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"               % "0.9.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"            % "3.0.2")
addSbtPlugin("com.thesamet"      % "sbt-protoc"            % "0.99.12")
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt-coursier" % "1.14")

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.6"
libraryDependencies += "org.slf4j"               % "slf4j-nop"      % "1.7.25"      // Needed by sbt-git

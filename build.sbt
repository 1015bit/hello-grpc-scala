// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `hello-grpc` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.13.4"
      val scalaTest  = "3.0.1"
    }
    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
  gitSettings ++
  headerSettings ++
  pbSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.1",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "io.ontherocks",
    licenses += ("Apache 2.0",
    url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) +=
      baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source",
      "1.8",
      "-target",
      "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) :=
      Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) :=
      Seq(scalaSource.in(Test).value)
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.license.Apache2_0
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2016", "Petra Bierleutgeb"))
  )

import com.trueaccord.scalapb.compiler.Version.scalapbVersion
lazy val pbSettings =
  Seq(
    PB.protoSources.in(Compile) := Seq(sourceDirectory.in(Compile).value / "proto"),
    PB.targets.in(Compile) := Seq(scalapb.gen() -> sourceManaged.in(Compile).value),
    libraryDependencies ++= Seq(
      "com.trueaccord.scalapb" %% "scalapb-runtime"      % scalapbVersion % "protobuf",
      "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion,
      "io.grpc"                % "grpc-netty"            % "1.1.2"
    )
  )

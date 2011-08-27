Kumiho
======

A web application for tailing multiple files.

Quick Start
-----------

    git clone URL/kumiho.git
    cd kumiho
    ./sbt update
    ./sbt jetty
    open http://localhost:54321

To change the port: project/build/LiftProject.scala
For other config:   src/main/scala/code/Config.scala

You can use environment variables like $HOME in filenames when 
adding log files.

To remove the log files and preferences: rm *.db


OSMPubGuide-Backend
=======
This repository is used to create an OSM based Pubguide, which deals with the aspects of an integration of temporal data. A customized API will be established, which will be used to provide additional, use case adapated information. On top of that, those temporal information should be stored within a database, which should be related to up-to-date OSM based information. A TileServer will serve Tiles, which focusses on specific entities.

# How to start - Check the Wiki :)

To get started with the OHBPGIOSM Backend Part, please refer to this wiki. You will find a detailed setup and configuration guide as well as some information about the general idea and architecture.

1. [System Overview (Idea, About, Architecture, Principles)](https://github.com/schlomm/OSMPubGuide-Backend/wiki/1.-System-Overview-(Ideas,-About,-Architecture,-Principles))
1. [Webserver Setup and Configuration Guide](https://github.com/schlomm/OSMPubGuide-Backend/wiki/2.-Apache-Webserver-Installation-and-Configuration)
1. [Database Setup and Configuration Guide](https://github.com/schlomm/OSMPubGuide-Backend/wiki/3.-postgreSQL-Installation-and-Configuration)
1. [Tomcat 7 Installation and Configuration Guide](https://github.com/schlomm/OSMPubGuide-Backend/wiki/4.-Tomcat-7-Installation-and-Configuration)
1. [TileMill 2 Installation and Configuration](https://github.com/schlomm/OSMPubGuide-Backend/wiki/5.-TileMill-2-Installation-and-Configuration)
1. [Vector Tiles via Docker Image](https://github.com/schlomm/OSMPubGuide-Backend/wiki/6.-Vectortiles-via-Docker-Container)
1. [API-Documentation](https://github.com/schlomm/OSMPubGuide-Backend/wiki/7.-API-documentation)
1. [Database Connection with JDBC and Netbeans](https://github.com/schlomm/OSMPubGuide-Backend/wiki/9.-Postgres-Database-Connection-with-JDBC-and-Netbeans).

----------

### Team Members:

 - [Andreas: aohrem][1] 
 - [Dominik: schlomm][2] 
 - [Fabian: froehr][3] 
 - [Ragnar: rw2013][4] 
 - [Stephanie: StephanieWalter][5]
 - [Christopher: ChristopherStephan][6]
 - [Ondrej: zvaraondrej][7]
 - [Florian: flahn][8]
 - [Tobias: TobiVanKenobi][18]

### Grouping

 - Webservice / API 
 - Database 
 - Tile Server 
 - OSM & Overpass API Documentation and Review (will be skipped after the conceptual phase is done)
 - Responsibilities:  Check Sprint Backlog

### Overall goals:

 - Check Sprint Backlog for detailed Tasks, Task-Assignments and User Stories [(click me)][15]
 - Deliverables: 
     - System mockup
     - Documentation of decisions and components (Check Sprint Backlog)
     - Use Case adapted API to provide additional information (temporal information). The API will also be able to query OSM data using the Overpass API.
     - Use Case adapted database to store temporal information.
     - Use Case adapted Tile Services, which focusses on meaningful and useful entities.

 
### Documents
 - Github Backend [(click me)][9] 
 - Github Frontend [(click me)][17]
 - Backlog: [(click me)][10] 
 - Sprint Backlog: [(click me)][11]
 - OHBPGIOSM-API Concept: [(click me)][12] 
 - Tile Server Visualization Concept: [(click me)][13] 
 - OSM / Overpass API Documentation: [(click me)][14]



### Architecture-Draft
![System Overview][16]
----------


### Sprint Overview

 - Sprint (1): 20.05.2014 -  10.06.2014 
     - Conceptional work (Visualization concept, database schema, API) 
     - Plans and detailed preparation
     - First rough prototypes (first implementation phase)
 - Sprint (2): 10.06.204 - End of June 
     - Actual start of implementation
     - Extend prototype 
 - Sprint (3): End of June -  Mid of July 
    - Actual implementation
    - More functionalities 
 - Sprint (4): Mid of June - End
     - Testing Finetuning Bugfixing

 
 
----------


## 1. Sprint: 20.05.2014 -  10.06.2014 ####	
**Organization**:

 - Scrum Master: Dominik 
 - Goals:
     - Conceptional work (Visualization concept, database schema, API concept)
     - Plans and detailed preparation
     - First rough prototypes (first implementation phase)
     - Server infrastructure

## 2. Sprint: 11.06.2014 -  01.07.2014 ####	
**Organization**:

 - Scrum Master: Dominik 
 - Goals:
     - Implementation Phase (Extending API Prototype, Tile Server, Database)
     - API: Extending Prototype with more functionalities
     - Tile Server: Instance of an own Tile Server with own style.
     - Database: Function to handle operations on server side.

 


  [1]: https://github.com/aohrem
  [2]: https://github.com/schlomm
  [3]: https://github.com/froehr
  [4]: https://github.com/rw2013
  [5]: https://github.com/StephanieWalter
  [6]: https://github.com/ChristopherStephan
  [7]: https://github.com/zvaraondrej
  [8]: https://github.com/flahn
  [9]: https://github.com/schlomm/OSMPubGuide-Backend
  [10]: https://docs.google.com/spreadsheet/ccc?key=0AjGDgpE0LC_sdGlNcUZwaXBmS2lzeHdlaXE5MHdzNmc&usp=drive_web#gid=0
  [11]: https://docs.google.com/spreadsheet/ccc?key=0AjGDgpE0LC_sdGlNcUZwaXBmS2lzeHdlaXE5MHdzNmc&usp=drive_web#gid=1
  [12]: https://docs.google.com/document/d/1HFcsoUxuWalOk8LrlJ1EaXs8oWmX2NwJKk7ynGSiEQM/edit
  [13]: https://docs.google.com/document/d/13aev6uE7L1icVZ2HV3IuSwRohakGlC3WVepFitKhlbE/edit
  [14]: https://docs.google.com/document/d/1P2jYhSZgxpkyV6LYOW1U9PtftiHgTfPnudfiNufbOow/edit
  [15]: https://docs.google.com/spreadsheet/ccc?key=0AjGDgpE0LC_sdGlNcUZwaXBmS2lzeHdlaXE5MHdzNmc&usp=drive_web#gid=1
  [16]: http://i.imgur.com/QjrW6RW.jpg
  [17]: https://github.com/MarkusKonk/OSMPubGuide
  [18]: https://github.com/tobivankenobi

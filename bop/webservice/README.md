Build Instructions
==================

Prerequisites: Java 8, Maven.

This builds, skipping tests, and produces jar files in target/.

    $ mvn package -DskipTests

Tests currently require Solr is already running with a bop-tests
collections. TODO automate that setup better.

Docker Instructions
===================

Prerequisites: Docker. And you must have built the 'jar' above.

Build an 'image' like so:

    $ docker build -t dwsmiley/hcga/bop-ws .
    
To run it (in the foreground):
    
    $ docker run --name bop-ws -p 8080:8080 --rm dwsmiley/hcga/bop-ws
    
Then point your browser at:
http://localhost:8080/bopws/swagger

To override configuration settings in bop-ws.yml, you can pass environment
variables via docker prefixed by "dw." (Dropwizard). And some are
special that don't use "dw.".  Examples:
     
    -e dw.server.applicationContextPath=/bopws
    -e dw.logging.level=DEBUG
    -e SOLR_HOST=192.168.0.25
    -e SOLR_PORT=8983

If you wish to use another configuration file altogether, add this option to docker:

    -v "`pwd`/bop-ws/bop-ws.yml:/opt/bop-ws/bop-ws.yml"

IMPORTANT: Of course this web-service needs to reach Solr, and by default
it expects it at localhost:8983.  That will very likely need to be
changed, since the docker image doesn't contain Solr (a bad idea).
You might need to supply the real IP or hostname of your machine.


TODO
====

The shaded jar is ~21MB. Perhaps we should produce an assembly with the
dependencies in their own directory, and with Dockerfile instructions that
result in a cached image that does *not* have the main jar.  That would
get a lot of cache hits for incremental improvement builds.  The jar
would be added in a separate instruction (and thus separate image layer).

Consider doing a DW "healthcheck" immediately on boot, thus failing
if Solr isn't up?
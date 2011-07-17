
repositories.remote = ["http://repo1.maven.org/maven2", "https://repository.jboss.org/nexus/content/repositories/central", "https://repository.jboss.org/nexus/content/repositories/snapshots", "https://repository.jboss.org/nexus/content/repositories/thirdparty-uploads", "http://release.intalio.com/m2repo", "http://www.intalio.org/public/maven2", "http://dist.codehaus.org/mule/dependencies/maven2", "http://repository.jboss.com/maven2", "http://repo1.maven.org/maven2", "http://mirrors.ibiblio.org/pub/mirrors/maven2" ]

repositories.release_to[:username] ||= "release"
repositories.release_to[:url] ||= "sftp://www.intalio.org/var/www-org/public/maven2"
repositories.release_to[:permissions] ||= 0664
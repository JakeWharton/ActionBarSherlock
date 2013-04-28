ActionBarSherlock Release Process
=================================

 1. Make sure she builds! `mvn clean verify`
 2. Ensure the `CHANGELOG.md` file has up-to-date information and the current date.
 3. Pull in the latest translations in the i18n module.
 4. Change all of the sample `AndroidManifest.xml` files to the correct version and bump the
    version code arbitrarily.
 5. `mvn clean release:clean && mvn release:prepare release:perform`
 6. Promote the Maven artifact on Sonatype's OSS Nexus install.
 7. Deploy website using `deploy_website.sh` script.

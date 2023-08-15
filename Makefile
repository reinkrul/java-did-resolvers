.PHONY: build

build:
	mvn clean package

release:
	echo "Remember to update version in README.md!"
	mvn clean
	mvn versions:set
	mvn deploy -Psign-artifacts -DaltDeploymentRepository=ossrh::default::https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
	open https://s01.oss.sonatype.org/
	echo "Remember to update version in README.md!"
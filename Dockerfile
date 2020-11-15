FROM jenkins/jenkins:latest

USER root

## install docker client
RUN curl -fsSL "https://download.docker.com/linux/static/stable/x86_64/docker-19.03.8.tgz" \
  | tar -xzC /usr/local/bin --strip=1 docker/docker

USER jenkins

ENV JAVA_OPTS               -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG     /var/jenkins_home/casc_configs
ENV JENKINS_UC              https://mirror.yandex.ru/mirrors/jenkins/updates
ENV JENKINS_UC_DOWNLOAD     https://mirror.yandex.ru/mirrors/jenkins
ENV JENKINS_UC_EXPERIMENTAL https://mirror.yandex.ru/mirrors/jenkins/updates/experimental
ENV JENKINS_PLUGIN_INFO     https://mirror.yandex.ru/mirrors/jenkins/updates/current/plugin-versions.json

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN /bin/jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

COPY --chown=jenkins:jenkins casc_configs /var/jenkins_home/casc_configs

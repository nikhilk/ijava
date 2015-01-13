FROM debian:jessie

# Setup Python, IPython and Java
RUN echo "deb-src http://ftp.us.debian.org/debian testing main" >> /etc/apt/sources.list && \
  apt-get update -y && \
  apt-get install --no-install-recommends -y -q \
    python2.7 python-pip python-dev \
    python-zmq python-tornado python-jinja2 && \
  pip install -U pip && \
  pip install -U tornado jinja2 && \
  pip install -U ipython==2.2.0 && \
  apt-get install --no-install-recommends -y -q openjdk-7-jdk

# Add the ijava application and associated files
ADD app app

# Configuration
EXPOSE 8080
VOLUME [ "/notebooks" ]
ENTRYPOINT [ "/app/start.sh" ]


FROM ubuntu:18.04
MAINTAINER rk3566-sdk "support@rk.com"
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && apt-get install -y \
    build-essential crossbuild-essential-arm64 \
    bash-completion vim nano sudo locales time rsync bc python \
    repo git ssh libssl-dev liblz4-tool lib32stdc++6 \
    expect patchelf chrpath gawk texinfo diffstat binfmt-support \
    qemu-user-static live-build bison flex fakeroot cmake unzip \
    device-tree-compiler python-pip ncurses-dev python-pyelftools \
    subversion asciidoc w3m dblatex graphviz python-matplotlib cpio \
    libparse-yapp-perl default-jre patchutils swig u-boot-tools bear \
    && apt-get clean

RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8

RUN useradd -c 'sdk user' -m -d /home/sdk -s /bin/bash sdk && \
    usermod -aG sudo sdk && \
    echo "sdk ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

USER sdk
WORKDIR /home/sdk


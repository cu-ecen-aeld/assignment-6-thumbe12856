# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-thumbe12856.git;protocol=ssh;branch=master"

PV = "1.0+git${SRCPV}"
SRCREV = "713c98cc2e930788b1ed30e6a29f0d1dc4c36d54"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git/server"

# Add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${bindir}/aesdsocket"
FILES:${PN} += "${sysconfdir}/init.d/aesdsocket-start-stop.sh"

INSANE_SKIP:${PN} += "ldflags"
TARGET_LDFLAGS += "-pthread -lrt"
RDEPENDS:${PN} += "libgcc"

do_configure () {
    :
}

do_compile () {
    oe_runmake
}

# Inherit the update-rc.d.bbclass file located in openembedded/classes/.
# This will take care of setting up startup links when the package is
# installed.
inherit update-rc.d

# Tell the update-rc.d package which program will be used as the startup
# script.
# The script will be called with the "start" command at system
# startup, the "stop" command at system shutdown, and the "restart" command
# when the package is updated.
INITSCRIPT_NAME = "aesdsocket-start-stop.sh"

# Run the command at step 90 during startup, and step 10 during shutdown.
# Because the numbers go from 00 - 99, larger numbers will be run later on.
# If your program is required for system startup, put a low number here.
# If it's user-facing or less critical for system startup, put a higher
# number here.
# As a tradition, the shutdown number should be 100-startup_number.  That
# way scripts are stopped in the reverse order they were started in.  Since
# our startup number here is 90, the shutdown number will be 100-90 or 10.
INITSCRIPT_PARAMS = "start 99 5 2 . stop 20 0 1 6 ."

do_install () {
    # Be sure to install the target directory with install -d first
    # Yocto variables ${D} and ${S} are useful here, which you can read about at
    # https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
    # and
    # https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
    # See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb

    install -d ${D}${bindir}
    install -m 0755 ${S}/aesdsocket ${D}${bindir}/

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/aesdsocket-start-stop.sh ${D}${sysconfdir}/init.d/
}

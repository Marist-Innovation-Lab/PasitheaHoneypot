#!/bin/bash

#This install Script presupposes that you are in the src directory of the SecureCloud REST honeypot git repo

#################################################################################################
#                         MARIST API HONEYPOT UBUNTU INSTALL SCRIPT v0.1                        #
#                               MARIST COLLEGE | IBM JOINT STUDY                                #
#                    ASSETS BORRWOED FROM MARIST COLLEGE NETWORKING DEPARTMENT                  #
#################################################################################################


# ASCII Art Variables  
RED='\e[0;31m'
RESET='\e[0m'

################################ Installation Functions ########################################

# Display Title Screen
function display_intro {
	echo -e "
                             /\      /\\
                             |\\\\\____//|
                             (|/    \/)
                             / (    ) \\
                ${RED}|||||||\\\\\\\  )   ${RESET}%)  (%   ${RED}(  ///||||||||
                ||           )  ${RESET}\\\\\  |/  ${RED}(           ||
                ||            )  ${RESET}\\\\\ |/  ${RED}(           ||
                  ||           /-- ${RESET}\@)${RED}--\         ||
                  ||       |              |       ||
                ||         ||            ||         ||
                ||         |||          |||         ||
                ||         ||||        ||||         ||
                |||||||||||||  \|    |/  |||||||||||||${RESET}
                       MARIST API HONEYPOT v0.1
            "

}

# Install required packages
function install_dependencies {
	echo -e "\nInstalling required packages..."

	apt-get update

	for i in default-jre default-jdk ; do
	  echo -en "\n>Installing $i...\n"
	  apt-get install -qq -y $i
	done
}

function check_root {
	if [ "$EUID" -ne 0 ]
	  then echo "Please run this script as root"
	  exit
	fi
}

function set_env {
	CHECK_JAVAHOME=$(grep -o "JAVA_HOME=.*" /etc/environment)
	if [[ -z $CHECK_JAVAHOME ]]
		then
		echo -e "Setting JAVA_HOME..."
		export JAVA_HOME=$(update-alternatives --config java | grep -o "/usr/lib/jvm/java-.-openjdk-amd64")
		echo "JAVA_HOME=${JAVA_HOME}" >> /etc/environment
	fi

	echo -e "Checking if you have an HPID..."
	CHECK_ID_ENVIROMENT=$(grep -oP "HPID=.*" /etc/environment | sed 's/HPID=//g')
	if [[ -z $CHECK_ID_ENVIROMENT ]]
	 then
	  echo -e "Generating your unique HPID..."
	  export HPID=$(dbus-uuidgen)
	  echo "HPID=${HPID}" >> /etc/environment
	fi
}

function port_config {
	if [[ -z ${1} ]]
		then
		PORT_NUM="8082"
	fi
	echo -e "Configuring port number..."
	sed -i "s/apiPort    = 8082/apiPort    = "$1"/" edu/marist/jointstudy/APIrest.java
}

#################################################################################################

check_root

#run the script after root is confirmed
display_intro
install_dependencies
set_env
echo -e "Please enter the port number you would like to configure the APIhp on. Press Enter to default to 8082."
read PORT_NUM
port_config ${PORT_NUM}

#compile the .java (possibly set classpaths)
echo -e "Compiling the java file..."
javac -cp ../lib/gson-2.2.2.jar:../lib/nanohttpd-2.2.0.jar:../nanohttpd-webserver-2.2.0.jar edu/marist/jointstudy/APIrest.java
#run the compiled program
echo -e "Starting up the APIhp..."
echo '#!/bin/bash
if [ "$EUID" -ne 0 ]
  then echo "Please run this script as root"
  exit
fi
cd "${0%/*}"
java -cp .:../lib/gson-2.2.2.jar:../lib/nanohttpd-2.2.0.jar:../nanohttpd-webserver-2.2.0.jar edu.marist.jointstudy.APIrest' >> runAPIrest.sh 

echo "Your API Honeypot is now installed. Please run the file 'runAPIrest.sh' to start your server. To run this server in the background, check out screen. To run this server on startup, check out the github documentation."

#################################################################################################

exit

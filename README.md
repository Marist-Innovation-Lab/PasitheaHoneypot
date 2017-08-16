# Pasithea: API Honeypot

### Installing Pasithea

Pasithea has a built in install script for easy installs on Ubuntu machines.

##### 1. Run the install script
```sh
$ cd PasitheaHoneypot/src
$ sudo ./install.sh
```
The script will now run the apt-get application manager and install the required dependencies.

You will eventually be prompted to enter the port number you would like Pasithea to run on.
```sh
Please enter the port number you would like to configure the APIhp on. Press Enter to default to 8080
<your port number here>
```
The install script will automatically compile the Java for you, and create a seperate script that will run the server for you.

##### 3. Run the server!
First, you must make the new script executable:
```sh
$ sudo chmod +x runAPIrest.sh
```
Then, run the server!
```sh
$ sudo ./runAPIrest.sh
```
After Pasithea has been deployed, any attempt on the API Honeypot will be logged in a '~' delimited log file named API.log in the src folder.

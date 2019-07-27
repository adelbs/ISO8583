# Adelbs-ISO8583
This is a gui tool for testing ISO8583 protocol (requests and responses), and a java library as well.
Through this tool you can:
* Configure the ISO8583 structure in both: visual (UI) or a very simple XML file;
* Invoke a ISO8583 service, sending a messages according to the XML configuration file;
* Receive a ISO8583 message from a external service and parse it to a readable format;
* Parse and analise messages.

The messages can be at many encoding formats, inclusive mainframe EBCDIC.

It is under development (not released yet), so you may find some UI bugs. 

Please:
* Open new Issues if you find any bug or if you have suggestions.
* Visit the Wiki (it's under construction).

<img src="https://raw.githubusercontent.com/adelbs/ISO8583/master/resources/img/snap01.png" width="400"> <img src="https://raw.githubusercontent.com/adelbs/ISO8583/master/resources/img/snap02.png" width="400">

<img src="https://raw.githubusercontent.com/adelbs/ISO8583/master/resources/img/snap03.png" width="400"> <img src="https://raw.githubusercontent.com/adelbs/ISO8583/master/resources/img/snap04.png" width="400">


## How to build

1- clone the project

	git clone https://github.com/adelbs/ISO8583.git

2- build executable JAR

	clean compile assembly:single